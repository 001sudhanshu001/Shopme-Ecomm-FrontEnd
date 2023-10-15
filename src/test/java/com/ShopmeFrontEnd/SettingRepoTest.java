package com.ShopmeFrontEnd;


import com.ShopmeFrontEnd.dao.SettingRepoFrontEnd;
import com.ShopmeFrontEnd.entity.readonly.Setting;
import com.ShopmeFrontEnd.entity.readonly.SettingCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SettingRepoTest {

    @Autowired
    SettingRepoFrontEnd repoFrontEnd;

    public void testFindByTwoCategories(){
        List<Setting> settings = repoFrontEnd.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.GENERAL);

        settings.forEach((setting) -> System.out.println(setting));
    }
}
