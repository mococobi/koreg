package com.mococo.microstrategy.sdk.prompt.dao;

import java.util.List;

import com.mococo.microstrategy.sdk.prompt.vo.PromptElement;

/**
 * MSTR 프롬프트 API를 이용하지 않는 DAO의 베이스 클래스
 * 
 * @author hyoungilpark
 *
 * @param <P1>
 * @param <P2>
 */
public class CustomPromptDao<P1, P2> implements PromptDao {
    // private static final Logger logger =
    // LoggerFactory.getLogger(PromptDao.class);

    private final P1 param1;
    private final P2 param2;

    public CustomPromptDao(P1 param1, P2 param2) {
        this.param1 = param1;
        this.param2 = param2;
    }

    protected P1 getParam1() {
        return param1;
    }

    protected P2 getParam2() {
        return param2;
    }

    @Override
    public int getPin() {
        return -1;
    }

    @Override
    public String getMin() {
        return null;
    }

    @Override
    public String getMax() {
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public boolean isRequired() {
        return false;
    }

    @Override
    public String getDefaultAnswer() {
        return null;
    }

    @Override
    public List<PromptElement> getDefaultAnswers() {
        return null;
    };

    @Override
    public List<PromptElement> getSuggestedAnswers(int level, String selectedElemId) {
        return null;
    }

    @Override
    public List<PromptElement> getSuggestedAnswers() {
        return null;
    }

    @Override
    public String getControlType() {
        return null;
    }

	@Override
	public int getPromptType() {
		return -1;
	}

	@Override
	public int getPromptSubType() {
		return -1;
	}

}
