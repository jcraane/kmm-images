/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.utils;


import org.checkerframework.checker.nullness.qual.NonNull;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * XML Utilities.
 * <p>
 * For Kotlin usage, many of these are exposed as more convenient extension
 * methods in DomExtensions.
 */
public class XmlUtils {
    // XML parser features
    private static final String NAMESPACE_PREFIX_FEATURE =
            "http://xml.org/sax/features/namespace-prefixes";
    private static final String PROVIDE_XMLNS_URIS =
            "http://xml.org/sax/features/xmlns-uris";
    private static final String LOAD_EXTERNAL_DTD =
            "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    private static final String EXTERNAL_PARAMETER_ENTITIES =
            "http://xml.org/sax/features/external-parameter-entities";
    private static final String EXTERNAL_GENERAL_ENTITIES =
            "http://xml.org/sax/features/external-general-entities";
    private static final String DISALLOW_DOCTYPE_DECL =
            "http://apache.org/xml/features/disallow-doctype-decl";

    /**
     * Formats the number and removes trailing zeros after the decimal dot and also the dot itself
     * if there were non-zero digits after it.
     *
     * @param value the value to be formatted
     * @return the corresponding XML string for the value
     */
    public static String formatFloatValue(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Invalid number: " + value);
        }
        // Use locale-independent conversion to make sure that the decimal separator is always dot.
        // We use Float.toString as opposed to Double.toString to avoid writing too many
        // insignificant digits.
        String result = Float.toString((float) value);
        return DecimalUtils.trimInsignificantZeros(result);
    }

    /** Strips out a leading UTF byte order mark, if present */
    @NonNull
    public static String stripBom(@NonNull String xml) {
        if (!xml.isEmpty() && xml.charAt(0) == '\uFEFF') {
            return xml.substring(1);
        }
        return xml;
    }

    public static SAXParserFactory configureSaxFactory(@NonNull SAXParserFactory factory,
                                                       boolean namespaceAware, boolean checkDtd) {
        try {
            factory.setXIncludeAware(false);
            factory.setNamespaceAware(namespaceAware); // http://xml.org/sax/features/namespaces
            factory.setFeature(NAMESPACE_PREFIX_FEATURE, namespaceAware);
            factory.setFeature(PROVIDE_XMLNS_URIS, namespaceAware);
            factory.setValidating(checkDtd);
        } catch (ParserConfigurationException | SAXException ignore) {
        }

        return factory;
    }

    @NonNull
    public static SAXParser createSaxParser(
            @NonNull SAXParserFactory factory,
            boolean allowDocTypeDeclarations) throws ParserConfigurationException, SAXException {
        SAXParser parser = factory.newSAXParser();
        XMLReader reader = parser.getXMLReader();

        // Prevent XML External Entity attack
        if (!allowDocTypeDeclarations) {
            // Most secure
            reader.setFeature(DISALLOW_DOCTYPE_DECL, true);
        } else {
            reader.setFeature(EXTERNAL_GENERAL_ENTITIES, false);
            reader.setFeature(EXTERNAL_PARAMETER_ENTITIES, false);
            reader.setFeature(LOAD_EXTERNAL_DTD, false);
        }

        return parser;
    }
}
