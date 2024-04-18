package com.writers.modelapp.setups.repository;

import com.writers.modelapp.setups.entity.ModulesDef;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ModulesDefRepo extends JpaRepository<ModulesDef, Long> {
    ModulesDef findByModuleNameIgnoreCase(String moduleName);

    ModulesDef findByModuleAlias(String id);

    @Query("SELECT s FROM ModulesDef s")
    List<ModulesDef> getModules(PageRequest request);

    @Query("SELECT s FROM ModulesDef s where s.moduleAlias = :id")
    List<ModulesDef> getModule(String id);
}
