/*
 * Copyright (C) 2019 The Android Open Source Project
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

package com.android;

/**
 * Constant definition class.<br>
 * <br>
 * Most constants have a prefix defining the content.
 * <ul>
 * <li><code>OS_</code> OS path constant. These paths are different depending on the platform.</li>
 * <li><code>FN_</code> File name constant.</li>
 * <li><code>FD_</code> Folder name constant.</li>
 * <li><code>TAG_</code> XML element tag name</li>
 * <li><code>ATTR_</code> XML attribute name</li>
 * <li><code>VALUE_</code> XML attribute value</li>
 * <li><code>CLASS_</code> Class name</li>
 * <li><code>DOT_</code> File name extension, including the dot </li>
 * <li><code>EXT_</code> File name extension, without the dot </li>
 * </ul>
 */
@SuppressWarnings({"javadoc", "unused"}) // Not documenting all the fields here
public final class SdkConstants {
    public static final String TAG_CLIP_PATH = "clip-path";
    public static final String PREFIX_RESOURCE_REF = "@"; //$NON-NLS-1$
    public static final String TAG_GROUP = "group"; //$NON-NLS-1$
    public static final String TAG_PATH = "path";
    public static final String TAG_VECTOR = "vector"; //$NON-NLS-1$
    public static final String UTF_8 = "UTF-8"; //$NON-NLS-1$
    public static final String DOT_XML = ".xml"; //$NON-NLS-1$
    public static final String TOOLS_URI = "http://schemas.android.com/tools"; //$NON-NLS-1$
}
