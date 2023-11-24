package com.custom.main.service;

import java.util.List;
import java.util.Map;

import com.mococo.microstrategy.sdk.esm.vo.MstrUser;

public interface MainService {

    public List<Map<String, Object>> getCorpFolder(MstrUser pMstrUser);
    
    public List<Map<String, Object>> getEquipmentProductFolder(MstrUser pMstrUser, String pFolderObjectId);
    
    public List<Map<String, Object>> getEnergySavingFolder(MstrUser pMstrUser, String pFolderObjectId);
}
