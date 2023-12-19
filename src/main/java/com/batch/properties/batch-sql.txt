<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
<properties>

	<entry key="select.eaim.department">
		SELECT *
		FROM (
			SELECT 
			      ognz.OGNZ_NO
			    , ognz.OGNZ_NM
			    , ognz.OGNZ_ABR_NM
			    , ognz.SPPO_OGNZ_NO
			    , ognz.VLDT_YMD
			    , ognz.PUSE_YN
			    ,(SELECT OGNZ_KD_CD FROM S_MSTRPTL.ZT_OGNZ WHERE OGNZ_NO = ognz.OGNZ_NO) AS OGNZ_KD_CD -- 1 : 영업조직, 2 : 본사조직, 3: 대리점조직, 4 : 영업산하조직(팀,지사), 9 : 기타,  Z : 불명
			    , (
			        SELECT
			            LISTAGG(OGNZ_NM, '//') WITHIN GROUP(ORDER BY SEQ DESC) AS USER_GROU_PATH
			        FROM(
			            SELECT 
			                OGNZ_NO, OGNZ_NM, LEVEL AS SEQ
			            FROM 
			                S_EIAM_MSTRPTL.AA_OGNZ 
			            START WITH 
			                OGNZ_NO = ognz.OGNZ_NO
			            CONNECT BY PRIOR 
			                SPPO_OGNZ_NO = OGNZ_NO
			                AND OGNZ_NO != SPPO_OGNZ_NO
			                AND OGNZ_NO != '0100001'
			        )
			    ) AS GROUP_PATH    
			    
			FROM 
			    S_EIAM_MSTRPTL.AA_OGNZ ognz
			WHERE 1=1
			    AND ognz.SPPO_OGNZ_NO != '0000000'
			    AND ognz.SPPO_OGNZ_NO IS NOT NULL
			    AND ognz.ognz_no != '0100001'
			ORDER BY
			    GROUP_PATH ASC
			    , OGNZ_NO DESC
		)
		WHERE 1 = 1
			AND  GROUP_PATH NOT LIKE '오렌지라이프%'
	</entry>


	<entry key="select.eaim.user">
		SELECT  * 
		FROM    (
				SELECT *
			    FROM   (
		                SELECT DISTINCT usr.PRAF_NO
		                                , usr.PRAF_NM
			                            , (
		                                    SELECT  * 
		                                    FROM    (
		                                                SELECT  *
		                                                FROM    (
		                                                            SELECT  PUSE_YN 
		                                                            FROM    S_EIAM_MSTRPTL.AA_USAC_USER_GROU_RTNS
		                                                            WHERE   PRAF_NO = usr.PRAF_NO
		                                                    
		                                                            UNION ALL
		                                                    
		                                                            SELECT  PUSE_YN 
		                                                            FROM    S_EIAM_IDS.AA_USAC_USER_GROU_RTNS
		                                                            WHERE   PRAF_NO = usr.PRAF_NO
		                                                        )
		                                                order by PUSE_YN desc
		                                            )
		                                    WHERE ROWNUM = 1
		                                   ) AS USER_PUSE_YN
		                                 , usr.OGNZ_NO
			                             , (
		                                    SELECT OGNZ_KD_CD
			                                FROM   S_MSTRPTL.ZT_OGNZ
			                                WHERE  ognz_no = usr.USER_GROU_ID
		                                    ) as OGNZ_KD_CD /* 1 : 영업조직, 2 : 본사조직, 3: 대리점조직, 4 : 영업산하조직(팀,지사), 9 : 기타,  Z : 불명 */
			                             , usr.USER_GROU_NM
		                                 , GROUP_PUSE_YN
			                             , rol.rolE_NO
			                             , rol.rolE_NM
			                             , rol.ASST_OWNR_rolE_TYPE_CD
			                             , rol.MASK_ECPT_MD_CD
			                             , rol.MAIN_CST_IFIN_YN
			                             , UGRL.PUSE_YN AS rolE_PUSE_YN
			                             , UGRL.VLDT_YMD AS ROLE_VLDT_YMD
			            FROM   (
		                        SELECT  a.*
			                            , b.OGNZ_NO as USER_GROU_ID
			                            , b.OGNZ_NM as USER_GROU_NM
			                            , c.OGNZ_KD_CD as test
			                            , b.PUSE_YN AS GROUP_PUSE_YN
			                    FROM    S_EIAM_MSTRPTL.AA_USAC a ,
			                            S_EIAM_MSTRPTL.AA_OGNZ b ,
			                            S_MSTRPTL.ZT_OGNZ c
			                    WHERE   a.OGNZ_NO = b.OGNZ_NO
			                    AND     a.OGNZ_NO = c.OGNZ_NO
			                    AND     b.OGNZ_NO = c.OGNZ_NO 
		                       ) usr 
			            INNER JOIN (
			                        SELECT   SYST_ID
		                                    , PRAF_NO
		                                    , USER_GROU_ID
		                                    , VLDT_YMD
				                            , (
				                                SELECT  PUSE_YN 
				                                FROM    S_EIAM_MSTRPTL.AA_USAC_USER_GROU_RTNS T2 
				                                WHERE   T1.SYST_ID = T2.SYST_ID
				                                AND     T1.PRAF_NO = T2.PRAF_NO
				                                AND     T1.USER_GROU_ID = T2.USER_GROU_ID
				                                AND     T1.VLDT_YMD = T2.VLDT_YMD
				                              ) AS PUSE_YN 
				                    FROM (
				                            SELECT  SYST_ID
		                                            , PRAF_NO
		                                            , USER_GROU_ID
		                                            , MAX(VLDT_YMD) AS VLDT_YMD
				                            FROM    S_EIAM_MSTRPTL.AA_USAC_USER_GROU_RTNS
				                            GROUP BY    SYST_ID
		                                                , PRAF_NO
		                                                , USER_GROU_ID
				                        ) T1
			                        ) UGRL ON UGRL.PRAF_NO = usr.PRAF_NO
			              INNER JOIN S_EIAM_MSTRPTL.AA_USER_GROU grp 
		                  ON    grp.SYST_ID = UGRL.SYST_ID
			              AND   grp.USER_GROU_ID = UGRL.USER_GROU_ID
			              AND   grp.PUSE_YN = 'Y' 
		                  INNER JOIN S_EIAM_MSTRPTL.AA_USER_GROU_rolE_RTNS urrl 
		                  ON    urrl.SYST_ID = UGRL.SYST_ID
			              AND   urrl.USER_GROU_ID = UGRL.USER_GROU_ID
			              AND   urrl.PUSE_YN = 'Y' 
		                  INNER JOIN S_EIAM_MSTRPTL.AA_rolE rol 
		                  ON    rol.SYST_ID = UGRL.SYST_ID
			              AND   rol.rolE_NO = urrl.rolE_NO
			              AND   rol.PUSE_YN = 'Y'
			                ORDER BY usr.PRAF_NO 
		            )
		            
			        UNION ALL
		            
					SELECT *
			        FROM   (
		                    SELECT DISTINCT usr.PRAF_NO
			                                , usr.PRAF_NM
			                                , (
		                                        SELECT *
		                                        FROM    (
		                                                SELECT *
		                                                FROM (
		                                                        SELECT  PUSE_YN 
		                                                        FROM    S_EIAM_MSTRPTL.AA_USAC_USER_GROU_RTNS
		                                                        WHERE   PRAF_NO = usr.PRAF_NO
		                                                                        
		                                                        UNION ALL
		                                                                        
		                                                        SELECT  PUSE_YN 
		                                                        FROM    S_EIAM_IDS.AA_USAC_USER_GROU_RTNS
		                                                        WHERE   PRAF_NO = usr.PRAF_NO
		                                                     )
		                                                ORDER BY PUSE_YN DESC
		                                                )
		                                        WHERE rownum = 1
		                                       ) AS USER_PUSE_YN
			                                , usr.OGNZ_NO
			                                , (
		                                        SELECT  OGNZ_KD_CD
			                                    FROM    S_MSTRPTL.ZT_OGNZ
			                                    WHERE   ognz_no = usr.USER_GROU_ID
		                                      ) as OGNZ_KD_CD   /* 1 : 영업조직, 2 : 본사조직, 3: 대리점조직, 4 : 영업산하조직(팀,지사), 9 : 기타,  Z : 불명 */
			                                , usr.USER_GROU_NM
			                                , GROUP_PUSE_YN
			                                , rol.rolE_NO
			                                , rol.rolE_NM
			                                , rol.ASST_OWNR_rolE_TYPE_CD
			                                , rol.MASK_ECPT_MD_CD
			                                , rol.MAIN_CST_IFIN_YN
			                                , UGRL.PUSE_YN AS rolE_PUSE_YN
			                                , UGRL.VLDT_YMD AS ROLE_VLDT_YMD
			                FROM   (
		                            SELECT a.*
			                               , b.OGNZ_NO as USER_GROU_ID
			                               , b.OGNZ_NM as USER_GROU_NM
			                               , c.OGNZ_KD_CD as test
			                               , b.PUSE_YN AS GROUP_PUSE_YN
			                        FROM   S_EIAM_IDS.AA_USAC a ,
			                               S_EIAM_MSTRPTL.AA_OGNZ b ,
			                               S_MSTRPTL.ZT_OGNZ c
			                        WHERE  1=1
			                        AND    a.OGNZ_NO = b.OGNZ_NO
			                        AND    a.OGNZ_NO = c.OGNZ_NO
			                        AND    b.OGNZ_NO = c.OGNZ_NO 
		                            ) usr 
			                INNER JOIN (
		                                SELECT  SYST_ID
		                                        , PRAF_NO
		                                        , USER_GROU_ID
		                                        , VLDT_YMD
				                                , (
				                                    SELECT  PUSE_YN 
				                                    FROM    S_EIAM_IDS.AA_USAC_USER_GROU_RTNS T2 
				                                    WHERE   T1.SYST_ID = T2.SYST_ID
				                                    AND     T1.PRAF_NO = T2.PRAF_NO
				                                    AND     T1.USER_GROU_ID = T2.USER_GROU_ID
				                                    AND     T1.VLDT_YMD = T2.VLDT_YMD
				                                  ) AS PUSE_YN 
				                        FROM    (
				                                    SELECT  SYST_ID
		                                                    , PRAF_NO
		                                                    , USER_GROU_ID
		                                                    , MAX(VLDT_YMD) AS VLDT_YMD
				                                    FROM    S_EIAM_IDS.AA_USAC_USER_GROU_RTNS
				                                    GROUP BY    SYST_ID
		                                                        , PRAF_NO
		                                                        , USER_GROU_ID
				                                ) T1
			                            ) UGRL 
		                    ON      UGRL.PRAF_NO    = usr.PRAF_NO
			                INNER JOIN S_EIAM_IDS.AA_USER_GROU grp
		                    ON      grp.SYST_ID     = UGRL.SYST_ID
			                AND     grp.USER_GROU_ID = UGRL.USER_GROU_ID
			                AND     grp.PUSE_YN = 'Y' INNER JOIN S_EIAM_IDS.AA_USER_GROU_rolE_RTNS urrl ON urrl.SYST_ID = UGRL.SYST_ID
			                AND     urrl.USER_GROU_ID = UGRL.USER_GROU_ID
			                AND     urrl.PUSE_YN = 'Y' INNER JOIN S_EIAM_IDS.AA_rolE rol ON rol.SYST_ID = UGRL.SYST_ID
			                AND     rol.rolE_NO = urrl.rolE_NO
			                AND     rol.PUSE_YN = 'Y'
			                ORDER BY usr.PRAF_NO 
		                    ) 
				)
		ORDER BY PRAF_NO
		        , USER_PUSE_YN
		        , ROLE_NM
		        , ROLE_VLDT_YMD
	</entry>

	<entry key="select.eaim.user_back">
		SELECT DISTINCT
		    usr.PRAF_NO,
		    usr.PRAF_NM,
		    usr.PUSE_YN AS USER_PUSE_YN,
		    usr.OGNZ_NO,
            usr.USER_GROU_PATH,
		    (select OGNZ_KD_CD from S_MSTRPTL.ZT_OGNZ where ognz_no = usr.USER_GROU_ID) as OGNZ_KD_CD,			-- 1 : 영업조직, 2 : 본사조직, 3: 대리점조직, 4 : 영업산하조직(팀,지사), 9 : 기타,  Z : 불명
		    usr.USER_GROU_NM,
		    GROUP_PUSE_YN,
		    rol.rolE_NO,
		    rol.rolE_NM,
		    rol.ASST_OWNR_rolE_TYPE_CD,
		    rol.MASK_ECPT_MD_CD,
		    rol.MAIN_CST_IFIN_YN,
		    rol.PUSE_YN AS rolE_PUSE_YN
		FROM
		    (
                select 
                    a.*, b.OGNZ_NO as USER_GROU_ID, b.OGNZ_NM as USER_GROU_NM, c.OGNZ_KD_CD as test, b.PUSE_YN AS GROUP_PUSE_YN
                , (
                    select
                        listagg(ognz_nm, '/') within group(order by seq desc) as USER_GROU_PATH
                    from(
                        select 
                            ognz_no, ognz_nm, level as seq
                        from 
                            S_EIAM_MSTRPTL.AA_OGNZ 
                        start with 
                            ognz_no = (select ognz_no from S_EIAM_MSTRPTL.AA_OGNZ where ognz_no = b.OGNZ_NO)
                        connect by prior 
                            sppo_ognz_no = ognz_no
                            and ognz_no != '0100001'
                    )
                ) AS USER_GROU_PATH
                from S_EIAM_MSTRPTL.AA_USAC a
                    , S_EIAM_MSTRPTL.AA_OGNZ b
                    , S_MSTRPTL.ZT_OGNZ c
                where 1=1
                    and a.OGNZ_NO = b.OGNZ_NO
                    and a.OGNZ_NO = c.OGNZ_NO
                    and b.OGNZ_NO = c.OGNZ_NO
		    ) usr
		INNER JOIN S_EIAM_MSTRPTL.AA_USAC_USER_GROU_RTNS UGRL
		    ON UGRL.PRAF_NO = usr.PRAF_NO
		    AND UGRL.PUSE_YN = 'Y'
		INNER JOIN S_EIAM_MSTRPTL.AA_USER_GROU grp
		    ON grp.SYST_ID = UGRL.SYST_ID
		    AND grp.USER_GROU_ID = UGRL.USER_GROU_ID
		    AND grp.PUSE_YN = 'Y'
		INNER JOIN S_EIAM_MSTRPTL.AA_USER_GROU_rolE_RTNS urrl
		    ON urrl.SYST_ID = UGRL.SYST_ID
		    AND urrl.USER_GROU_ID = UGRL.USER_GROU_ID
		    AND urrl.PUSE_YN = 'Y'
		INNER JOIN S_EIAM_MSTRPTL.AA_rolE rol
		    ON rol.SYST_ID = UGRL.SYST_ID
		    AND rol.rolE_NO = urrl.rolE_NO
		    AND rol.PUSE_YN = 'Y'
		ORDER BY 
		    usr.PRAF_NO
	</entry>


	<entry key="select.eaim.user.back">
		SELECT DISTINCT
		    usr.PRAF_NO,
		    usr.PRAF_NM,
		    usr.PUSE_YN AS USER_PUSE_YN,
		    usr.OGNZ_NO,
		    ognz.OGNZ_KD_CD as OGNZ_KD_CD,							-- 1 : 영업조직, 2 : 본사조직, 3: 대리점조직, 4 : 영업산하조직(팀,지사), 9 : 기타,  Z : 불명
		    --usr.PRAF_OFDU_NM AS USER_GROU_NM,
		    ognz.OGNZ_NM AS USER_GROU_NM,
            ognz.sppo_ognz_no,
   		    'EIAM그룹/' || (
                select
                    case 
                        when listagg(ognz_nm, '/') within group(order by "level" desc) = ognz.OGNZ_NM then ognz.OGNZ_NM 
                        else listagg(ognz_nm, '/') within group(order by "level" desc) || '/' ||ognz.OGNZ_NM end 
                from (
                    select 
                        distinct ognz_no, ognz_nm, "level"
                    from (
                            select 
                                ognz_no, ognz_nm, 
                                
                                case when (
                                 select 
                                    count(ognz_no)
                                 from 
                                    S_MSTRPTL.ZT_OGNZ
                                 where 1=1
                                    and ognz_no = ognz.OGNZ_NO
                                    and ognz_no = sppo_ognz_no
                                ) = 1 then 1
                                else (
                                    select count(ognz_no) + 1 from 
                                            (
                                            select 
                                                ognz_no
                                            from 
                                                S_MSTRPTL.ZT_OGNZ 
                                            start with 
                                                ognz_no = ognz.OGNZ_NO
                                            connect by prior 
                                                sppo_ognz_no = ognz_no
                                                and sppo_ognz_no != ognz_no
                                        )
                                )
                                 
                                end 
                                as "level"
                            from 
                                S_MSTRPTL.ZT_OGNZ 
                            where
                                ognz_no = ognz.sppo_ognz_no  

                    ) 
                    order by 
                        "level" desc
                
                )
            ) AS USER_GROU_PATH,
		    'Y' AS GROUP_PUSE_YN,
		    rol.rolE_NO,
		    rol.rolE_NM,
		    rol.ASST_OWNR_rolE_TYPE_CD,
		    rol.MASK_ECPT_MD_CD,
		    rol.MAIN_CST_IFIN_YN,
		    rol.PUSE_YN AS rolE_PUSE_YN
		FROM
		    S_EIAM_MSTRPTL.AA_USAC usr
		INNER JOIN S_EIAM_MSTRPTL.AA_USAC_USER_GROU_RTNS UGRL
		    ON UGRL.PRAF_NO = usr.PRAF_NO
		    AND UGRL.PUSE_YN = 'Y'
		INNER JOIN S_EIAM_MSTRPTL.AA_USER_GROU grp
		    ON grp.SYST_ID = UGRL.SYST_ID
		    AND grp.USER_GROU_ID = UGRL.USER_GROU_ID
			AND grp.PUSE_YN = 'Y'
		INNER JOIN S_EIAM_MSTRPTL.AA_USER_GROU_rolE_RTNS urrl
		    ON urrl.SYST_ID = UGRL.SYST_ID
		    AND urrl.USER_GROU_ID = UGRL.USER_GROU_ID
			AND urrl.PUSE_YN = 'Y'
		INNER JOIN S_EIAM_MSTRPTL.AA_rolE rol
		    ON rol.SYST_ID = UGRL.SYST_ID
		    AND rol.rolE_NO = urrl.rolE_NO
			AND rol.PUSE_YN = 'Y'
		INNER JOIN S_MSTRPTL.ZT_OGNZ ognz
            ON USR.OGNZ_NO = OGNZ.OGNZ_NO
		ORDER BY 
			PRAF_NO
	</entry>
	
	
	
	<entry key="select.eaim.user1">
	
		select 
		      usr.PRAF_NO
		    , usr.PRAF_NM
		    , usr.PUSE_YN AS USER_PUSE_YN
		    , (
		        select
		            listagg(rol1.rolE_NM, ',') within group(order by rol1.rolE_NM)
		        from   
		            S_EIAM_MSTRPTL.AA_USAC usr1
		            INNER JOIN S_EIAM_MSTRPTL.AA_USAC_USER_GROU_RTNS UGRL1
		                ON UGRL1.PRAF_NO = usr1.PRAF_NO
		                AND UGRL1.PUSE_YN = 'Y'
		            INNER JOIN S_EIAM_MSTRPTL.AA_USER_GROU grp1
		                ON grp1.SYST_ID = UGRL1.SYST_ID
		                AND grp1.USER_GROU_ID = UGRL1.USER_GROU_ID
		                AND grp1.PUSE_YN = 'Y'
		            INNER JOIN S_EIAM_MSTRPTL.AA_USER_GROU_rolE_RTNS urrl1
		                ON urrl1.SYST_ID = UGRL1.SYST_ID
		                AND urrl1.USER_GROU_ID = UGRL1.USER_GROU_ID
		                AND urrl1.PUSE_YN = 'Y'
		            INNER JOIN S_EIAM_MSTRPTL.AA_rolE rol1
		                ON rol1.SYST_ID = UGRL1.SYST_ID
		                AND rol1.rolE_NO = urrl1.rolE_NO
		                AND rol1.PUSE_YN = 'Y'
		        where
		            usr1.PRAF_NO = usr.PRAF_NO
		            and rol1.rolE_NO in('BI_MSTR_NORMAL', 'BI_MSTR_POWER')
		    ) AS FN_GROUP_NM
		    , (
		        select
		            listagg(rol1.rolE_NM, ',') within group(order by rol1.rolE_NM)
		        from   
		            S_EIAM_MSTRPTL.AA_USAC usr1
		            INNER JOIN S_EIAM_MSTRPTL.AA_USAC_USER_GROU_RTNS UGRL1
		                ON UGRL1.PRAF_NO = usr1.PRAF_NO
		                AND UGRL1.PUSE_YN = 'Y'
		            INNER JOIN S_EIAM_MSTRPTL.AA_USER_GROU grp1
		                ON grp1.SYST_ID = UGRL1.SYST_ID
		                AND grp1.USER_GROU_ID = UGRL1.USER_GROU_ID
		                AND grp1.PUSE_YN = 'Y'
		            INNER JOIN S_EIAM_MSTRPTL.AA_USER_GROU_rolE_RTNS urrl1
		                ON urrl1.SYST_ID = UGRL1.SYST_ID
		                AND urrl1.USER_GROU_ID = UGRL1.USER_GROU_ID
		                AND urrl1.PUSE_YN = 'Y'
		            INNER JOIN S_EIAM_MSTRPTL.AA_rolE rol1
		                ON rol1.SYST_ID = UGRL1.SYST_ID
		                AND rol1.rolE_NO = urrl1.rolE_NO
		                AND rol1.PUSE_YN = 'Y'
		        where
		            usr1.PRAF_NO = usr.PRAF_NO
		            and rol1.rolE_NO in('BI_MSTR_SYS_MANAGER', 'BI_MSTR_DEVELOPER')
		    ) AS SM_GROUP_NM
		    , (
		        select
		            listagg(rol1.rolE_NM, ',') within group(order by rol1.rolE_NM)
		        from   
		            S_EIAM_MSTRPTL.AA_USAC usr1
		            INNER JOIN S_EIAM_MSTRPTL.AA_USAC_USER_GROU_RTNS UGRL1
		                ON UGRL1.PRAF_NO = usr1.PRAF_NO
		                AND UGRL1.PUSE_YN = 'Y'
		            INNER JOIN S_EIAM_MSTRPTL.AA_USER_GROU grp1
		                ON grp1.SYST_ID = UGRL1.SYST_ID
		                AND grp1.USER_GROU_ID = UGRL1.USER_GROU_ID
		                AND grp1.PUSE_YN = 'Y'
		            INNER JOIN S_EIAM_MSTRPTL.AA_USER_GROU_rolE_RTNS urrl1
		                ON urrl1.SYST_ID = UGRL1.SYST_ID
		                AND urrl1.USER_GROU_ID = UGRL1.USER_GROU_ID
		                AND urrl1.PUSE_YN = 'Y'
		            INNER JOIN S_EIAM_MSTRPTL.AA_rolE rol1
		                ON rol1.SYST_ID = UGRL1.SYST_ID
		                AND rol1.rolE_NO = urrl1.rolE_NO
		                AND rol1.PUSE_YN = 'Y'
		        where
		            usr1.PRAF_NO = usr.PRAF_NO
		            and rol1.rolE_NO in('BI_MSTR_DATA_SP01', 'BI_MSTR_DATA_SP02', 'BI_MSTR_DATA_SP03')
		    ) AS DA_GROUP_NM
		    , usr.OGNZ_NO
		    , ognz.OGNZ_NM
		    , 'EIAM그룹/' || (
		        select
		            case 
		                when listagg(ognz_nm, '/') within group(order by "level" desc) = ognz.OGNZ_NM then ognz.OGNZ_NM 
		                else listagg(ognz_nm, '/') within group(order by "level" desc) || '/' ||ognz.OGNZ_NM end 
		        from (
		            select 
		                distinct ognz_no, ognz_nm, "level"
		            from (
		                    select 
		                        ognz_no, ognz_nm, 
		                        
		                        case when (
		                         select 
		                            count(ognz_no)
		                         from 
		                            S_MSTRPTL.ZT_OGNZ
		                         where 1=1
		                            and ognz_no = ognz.OGNZ_NO
		                            and ognz_no = sppo_ognz_no
		                        ) = 1 then 1
		                        else (
		                            select count(ognz_no) + 1 from 
		                                    (
		                                    select 
		                                        ognz_no
		                                    from 
		                                        S_MSTRPTL.ZT_OGNZ 
		                                    start with 
		                                        ognz_no = ognz.OGNZ_NO
		                                    connect by prior 
		                                        sppo_ognz_no = ognz_no
		                                        and sppo_ognz_no != ognz_no
		                                )
		                        )
		                         
		                        end 
		                        as "level"
		                    from 
		                        S_MSTRPTL.ZT_OGNZ 
		                    where
		                        ognz_no = ognz.sppo_ognz_no  
		
		            ) 
		            order by 
		                "level" desc
		        
		        )
		    ) AS USER_GROU_PATH
		from 
		    S_EIAM_MSTRPTL.AA_USAC usr
		    INNER JOIN S_MSTRPTL.ZT_OGNZ ognz
		        ON USR.OGNZ_NO = OGNZ.OGNZ_NO
	</entry>
	
	
	<entry key="select.eaim.user.test">
		select 
		      usr.PRAF_NO
		    , usr.PRAF_NM
		    , usr.PUSE_YN AS USER_PUSE_YN
		    , usr.OGNZ_NO
		    , ognz.OGNZ_NM AS USER_GROU_NM
		    , 'EIAM그룹/' || (
		        select
		            case 
		                when listagg(ognz_nm, '/') within group(order by "level" desc) = ognz.OGNZ_NM then ognz.OGNZ_NM 
		                else listagg(ognz_nm, '/') within group(order by "level" desc) || '/' ||ognz.OGNZ_NM end 
		        from (
		            select 
		                distinct ognz_no, ognz_nm, "level"
		            from (
		                    select 
		                        ognz_no, ognz_nm, 
		                        
		                        case when (
		                         select 
		                            count(ognz_no)
		                         from 
		                            S_MSTRPTL.ZT_OGNZ
		                         where 1=1
		                            and ognz_no = ognz.OGNZ_NO
		                            and ognz_no = sppo_ognz_no
		                        ) = 1 then 1
		                        else (
		                            select count(ognz_no) + 1 from 
		                                    (
		                                    select 
		                                        ognz_no
		                                    from 
		                                        S_MSTRPTL.ZT_OGNZ 
		                                    start with 
		                                        ognz_no = ognz.OGNZ_NO
		                                    connect by prior 
		                                        sppo_ognz_no = ognz_no
		                                        and sppo_ognz_no != ognz_no
		                                )
		                        )
		                         
		                        end 
		                        as "level"
		                    from 
		                        S_MSTRPTL.ZT_OGNZ 
		                    where
		                        ognz_no = ognz.sppo_ognz_no  
		
		            ) 
		            order by 
		                "level" desc
		        
		        )
		    ) AS USER_GROU_PATH
		    , (
		        select
		            listagg(rol1.rolE_NM, ',') within group(order by rol1.rolE_NM)
		        from   
		            S_EIAM_MSTRPTL.AA_USAC usr1
		            INNER JOIN S_EIAM_MSTRPTL.AA_USAC_USER_GROU_RTNS UGRL1
		                ON UGRL1.PRAF_NO = usr1.PRAF_NO
		                AND UGRL1.PUSE_YN = 'Y'
		            INNER JOIN S_EIAM_MSTRPTL.AA_USER_GROU grp1
		                ON grp1.SYST_ID = UGRL1.SYST_ID
		                AND grp1.USER_GROU_ID = UGRL1.USER_GROU_ID
		                AND grp1.PUSE_YN = 'Y'
		            INNER JOIN S_EIAM_MSTRPTL.AA_USER_GROU_rolE_RTNS urrl1
		                ON urrl1.SYST_ID = UGRL1.SYST_ID
		                AND urrl1.USER_GROU_ID = UGRL1.USER_GROU_ID
		                AND urrl1.PUSE_YN = 'Y'
		            INNER JOIN S_EIAM_MSTRPTL.AA_rolE rol1
		                ON rol1.SYST_ID = UGRL1.SYST_ID
		                AND rol1.rolE_NO = urrl1.rolE_NO
		                AND rol1.PUSE_YN = 'Y'
		        where
		            usr1.PRAF_NO = usr.PRAF_NO
		    ) AS group_NM_Y
		   , (
		    select
		        listagg(rol1.rolE_NM, ',') within group(order by rol1.rolE_NM)
		    from   
		        S_EIAM_MSTRPTL.AA_USAC usr1
		        INNER JOIN S_EIAM_MSTRPTL.AA_USAC_USER_GROU_RTNS UGRL1
		            ON UGRL1.PRAF_NO = usr1.PRAF_NO
		            AND UGRL1.PUSE_YN = 'Y'
		        INNER JOIN S_EIAM_MSTRPTL.AA_USER_GROU grp1
		            ON grp1.SYST_ID = UGRL1.SYST_ID
		            AND grp1.USER_GROU_ID = UGRL1.USER_GROU_ID
		            AND grp1.PUSE_YN = 'Y'
		        INNER JOIN S_EIAM_MSTRPTL.AA_USER_GROU_rolE_RTNS urrl1
		            ON urrl1.SYST_ID = UGRL1.SYST_ID
		            AND urrl1.USER_GROU_ID = UGRL1.USER_GROU_ID
		            AND urrl1.PUSE_YN = 'Y'
		        INNER JOIN S_EIAM_MSTRPTL.AA_rolE rol1
		            ON rol1.SYST_ID = UGRL1.SYST_ID
		            AND rol1.rolE_NO = urrl1.rolE_NO
		            AND rol1.PUSE_YN = 'N'
		    where
		        usr1.PRAF_NO = usr.PRAF_NO
		    ) AS group_NM_N
		from 
		    S_EIAM_MSTRPTL.AA_USAC usr
		    INNER JOIN S_MSTRPTL.ZT_OGNZ ognz
		            ON USR.OGNZ_NO = OGNZ.OGNZ_NO
	</entry>




</properties>
