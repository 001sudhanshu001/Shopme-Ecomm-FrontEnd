package com.ShopmeFrontEnd.dao;

import com.ShopmeFrontEnd.entity.readonly.Setting;
import com.ShopmeFrontEnd.entity.readonly.SettingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SettingRepoFrontEnd extends JpaRepository<Setting, String> {
    List<Setting> findByCategory(SettingCategory category);

    @Query("SELECT s FROM Setting s WHERE s.category = ?1 OR s.category = ?2")
    List<Setting> findByTwoCategories(SettingCategory cat1, SettingCategory cat2);
}
