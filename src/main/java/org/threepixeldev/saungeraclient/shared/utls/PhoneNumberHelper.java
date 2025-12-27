package org.threepixeldev.saungeraclient.shared.utls;

public class PhoneNumberHelper {
    public static String normalizePhoneNumber(String rawInput) {
        if (rawInput == null) return null;
        String digits = rawInput.replaceAll("[^0-9]", "");
        if (digits.startsWith("959")) {
            return "+" + digits;
        }
        if (digits.startsWith("09")) {
            return "+95" + digits.substring(1);
        }
        if (digits.startsWith("9")) {
            return "+95" + digits;
        }
        return "+" + digits;
    }
}
