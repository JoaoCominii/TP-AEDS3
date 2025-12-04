package util;

import java.util.HashMap;
import java.util.Map;

public class StringSearch {

    // KMP: returns true if pattern is found in text
    public static boolean kmpContains(String text, String pattern) {
        if (pattern == null || pattern.isEmpty()) return true;
        if (text == null || text.isEmpty()) return false;
        int[] lps = buildLPS(pattern);
        int i = 0, j = 0;
        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++; j++;
                if (j == pattern.length()) return true;
            } else {
                if (j != 0) j = lps[j-1]; else i++;
            }
        }
        return false;
    }

    private static int[] buildLPS(String pat) {
        int n = pat.length();
        int[] lps = new int[n];
        lps[0] = 0;
        int len = 0;
        int i = 1;
        while (i < n) {
            if (pat.charAt(i) == pat.charAt(len)) {
                len++; lps[i] = len; i++;
            } else {
                if (len != 0) len = lps[len-1]; else { lps[i] = 0; i++; }
            }
        }
        return lps;
    }

    // Boyer-Moore (bad character rule only)
    public static boolean bmContains(String text, String pattern) {
        if (pattern == null || pattern.isEmpty()) return true;
        if (text == null || text.isEmpty()) return false;
        Map<Character, Integer> last = buildLastOccurrence(pattern);
        int n = text.length();
        int m = pattern.length();
        // Preprocess good-suffix rule
        int[] suffix = new int[m];
        boolean[] prefix = new boolean[m];
        buildGoodSuffix(pattern, suffix, prefix);

        int i = 0;
        while (i <= n - m) {
            int j = m - 1;
            while (j >= 0 && pattern.charAt(j) == text.charAt(i + j)) j--;
            if (j < 0) return true; // match
            // bad-character shift
            char bad = text.charAt(i + j);
            Integer lo = last.get(bad);
            int badShift = (lo == null) ? j + 1 : Math.max(1, j - lo);
            // good-suffix shift
            int gsShift = moveByGoodSuffix(j, m, suffix, prefix);
            int shift = Math.max(badShift, gsShift);
            i += shift;
        }
        return false;
    }

    private static Map<Character, Integer> buildLastOccurrence(String pat) {
        Map<Character, Integer> last = new HashMap<>();
        for (int i = 0; i < pat.length(); i++) {
            last.put(pat.charAt(i), i);
        }
        return last;
    }

    // Build good-suffix tables: suffix[k] = starting index of the substring in pat
    // that is a suffix of length k; prefix[k] is true if a suffix of length k
    // is also a prefix of pat.
    private static void buildGoodSuffix(String pat, int[] suffix, boolean[] prefix) {
        int m = pat.length();
        for (int i = 0; i < m; i++) {
            suffix[i] = -1;
            prefix[i] = false;
        }
        for (int i = 0; i < m - 1; i++) {
            int j = i;
            int k = 0; // length of matching suffix
            while (j >= 0 && pat.charAt(j) == pat.charAt(m - 1 - k)) {
                j--;
                k++;
                suffix[k] = j + 1;
            }
            if (j == -1) prefix[k] = true;
        }
    }

    // Calculate shift using good-suffix rule when mismatch at position j
    private static int moveByGoodSuffix(int j, int m, int[] suffix, boolean[] prefix) {
        int k = m - 1 - j; // length of suffix matched
        if (k == 0) return 1; // no good suffix
        // If there is a substring in pattern which is a suffix of length k
        if (suffix[k] != -1) {
            return j - suffix[k] + 1;
        }
        // Otherwise, find the smallest r such that prefix of length m - r is suffix
        for (int r = j + 2; r <= m - 1; r++) {
            if (prefix[m - r]) {
                return r;
            }
        }
        return m;
    }
}
