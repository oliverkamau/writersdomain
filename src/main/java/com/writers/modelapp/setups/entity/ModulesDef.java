package com.writers.modelapp.setups.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name ="writers_app_modules")
@Data
public class ModulesDef {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="module_code")
    private Long moduleCode;

    @Column(name="module_sht_desc",nullable = false)
    private String moduleDesc;

    @Column(name="module_name",nullable = false)
    private String moduleName;

    @Column(name="module_alias")
    private String moduleAlias;

}
