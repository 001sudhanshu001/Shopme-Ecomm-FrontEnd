package com.ShopmeFrontEnd.entity.readonly;


import com.ShopmeFrontEnd.dao.SettingRepoFrontEnd;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SettingService {
    private SettingRepoFrontEnd settingRepoFrontEnd;

    public List<Setting> listAllSetting() {
        return settingRepoFrontEnd.findAll();
    }

    public List<Setting> getGeneralSettings() {
        return settingRepoFrontEnd.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
    }

}
