/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.utils.chathandlers;

/**
 * @author hex1r0
 */
public class AdminCommandHandler extends ChatCommandHandler {

    public AdminCommandHandler(ChatCommandRegistry registry, ChatCommandSecurity security) {
        super(registry, security);
    }

    @Override
    public String getPrefix() {
        return "//";
    }

}
