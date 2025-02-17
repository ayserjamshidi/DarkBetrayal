/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.model.templates.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.StringUtils;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MailPart")
@XmlSeeAlso({Sender.class, Header.class, Body.class, Tail.class, Title.class})
public abstract class MailPart extends StringParamList implements IMailFormatter {

    @XmlAttribute(name = "id")
    protected Integer id;

    @Override
    public MailPartType getType() {
        return MailPartType.CUSTOM;
    }

    public Integer getId() {
        return id;
    }

    public String getFormattedString(IMailFormatter customFormatter) {
        String result = "";
        IMailFormatter formatter = this;
        if (customFormatter != null) {
            formatter = customFormatter;
        }

        result = getFormattedString(getType());

        String[] paramValues = new String[getParam().size()];
        for (int i = 0; i < getParam().size(); i++) {
            StringParamList.Param param = getParam().get(i);
            paramValues[i] = formatter.getParamValue(param.getId());
        }
        String joinedParams = StringUtils.join(paramValues, ',');
        if (StringUtils.isEmpty(result)) {
            return joinedParams;
        }
        if (!StringUtils.isEmpty(joinedParams)) {
            result = result + "," + joinedParams;
        }
        return result;
    }

    @Override
    public String getFormattedString(MailPartType partType) {
        String result = "";
        if (id > 0) {
            result = result + id.toString();
        }
        return result;
    }
}
