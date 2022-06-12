package com.github.sky295

import taboolib.common.platform.Plugin
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration

object ResidencePlus : Plugin() {

    @Config("config.yml", true, autoReload = true)
    lateinit var config: Configuration

    @Config("data.yml", true, autoReload = true)
    lateinit var data: Configuration
}