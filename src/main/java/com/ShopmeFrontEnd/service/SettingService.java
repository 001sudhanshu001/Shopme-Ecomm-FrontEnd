package com.ShopmeFrontEnd.service;


import com.ShopmeFrontEnd.dao.SettingRepoFrontEnd;
import com.ShopmeFrontEnd.entity.readonly.EmailSettingBag;
import com.ShopmeFrontEnd.entity.readonly.Setting;
import com.ShopmeFrontEnd.entity.readonly.SettingCategory;
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

    public EmailSettingBag getEmailSetting() {
        List<Setting> settings = settingRepoFrontEnd.findByCategory(SettingCategory.MAIL_SERVER);

        settings.addAll(settingRepoFrontEnd.findByCategory(SettingCategory.MAIL_TEMPLATE));

        return new EmailSettingBag(settings);
    }
}
