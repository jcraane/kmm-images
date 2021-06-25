/*
 * Copyright (C) 2017 The Android Open Source Project
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

/**
 * A number of utility methods around {@link CharSequence} handling, which
 * adds methods that are available on Strings (such as {@code indexOf},
 * {@code startsWith} and {@code regionMatches} and provides equivalent methods
 * for character sequences.
 * <p>
 * <b>NOTE: This is not a public or final API; if you rely on this be prepared
 * to adjust your code for the next tools release.</b>
 */
public class CharSequences {
    /** Similar to {@link String#indexOf(int, int)} but with case insensitive comparison. */
    public static int indexOfIgnoreCase(
            CharSequence where,  CharSequence what, int fromIndex) {
        int targetCount = what.length();
        int sourceCount = where.length();

        if (fromIndex >= sourceCount) {
            return targetCount == 0 ? sourceCount : -1;
        }

        if (fromIndex < 0) {
            fromIndex = 0;
        }

        if (targetCount == 0) {
            return fromIndex;
        }

        char first = what.charAt(0);
        int max = sourceCount - targetCount;

        for (int i = fromIndex; i <= max; i++) {
            /* Look for first character. */
            if (!charsEqualIgnoreCase(where.charAt(i), first)) {
                //noinspection StatementWithEmptyBody,AssignmentToForLoopParameter
                while (++i <= max && !charsEqualIgnoreCase(where.charAt(i), first)) {}
            }

            /* Found first character, now look at the rest of "what". */
            if (i <= max) {
                int j = i + 1;
                int end = j + targetCount - 1;
                //noinspection StatementWithEmptyBody
                for (int k = 1;
                     j < end && charsEqualIgnoreCase(where.charAt(j), what.charAt(k));
                     j++, k++) {}

                if (j == end) {
                    /* Found whole string. */
                    return i;
                }
            }
        }

        return -1;
    }

    private static boolean charsEqualIgnoreCase(char c1, char c2) {
        // Conversion to upper case alone is not sufficient, for example for Georgian alphabet.
        return toUpperCase(c1) == toUpperCase(c2) || toLowerCase(c1) == toLowerCase(c2);
    }

    /**
     * Converts a character to upper case. A slightly optimized version of
     * {@link Character#toUpperCase(char)}.
     */
    public static char toUpperCase(char c) {
        if (c < 'a') return c;
        if (c <= 'z') return (char) (c + ('A' - 'a'));
        return Character.toUpperCase(c);
    }

    /**
     * Converts a character to lower case. A slightly optimized version of
     * {@link Character#toLowerCase(char)}.
     */
    public static char toLowerCase(char c) {
        if (c < 'A' || c >= 'a' && c <= 'z') return c;
        if (c <= 'Z') return (char) (c + ('a' - 'A'));
        return Character.toLowerCase(c);
    }
}
