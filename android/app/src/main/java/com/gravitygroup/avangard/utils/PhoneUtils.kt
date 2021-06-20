package com.gravitygroup.avangard.utils

object PhoneUtils {

    /**
     * Форматирует номер телефона в формат +7 (ххх) ххх-хх-хх. Если телефон начинается с 7, 8 и содержит 11 цифр или если телефон содержит 10 цифр,
     * то форматирование выполняется, иначе возвращается первоначальный номер.
     *
     * @param number номер телефона
     * @param withBracket флаг добавления символом ( и ) в отформатированный телефон
     * @return
     */
    fun formatPhoneNumber(number: String?, withBracket: Boolean = true): String? {
        if (number == null) {
            return null
        }
        val rawNumber = getRawPhoneNumber(number)
        if (rawNumber.length != FULL_RAW_NUMBER_LENGTH) {
            return number
        }

        val result = StringBuilder()
        if (withBracket) {
            result.append(PRETTY_PREFIX)
        } else {
            result.append("$PLUS_SEVEN_PREFIX ")
        }
        result.append(rawNumber.substring(CODE_BLOCK_START, CODE_BLOCK_END))
        if (withBracket) {
            result.append(CODE_BLOCK_PRETTY_FINISH)
        } else {
            result.append(" ")
        }
        result.append(rawNumber.substring(FIRST_BLOCK_START, FIRST_BLOCK_END))
        result.append(DIGITS_DELIMITER)
        result.append(rawNumber.substring(SECOND_BLOCK_START, SECOND_BLOCK_END))
        result.append(DIGITS_DELIMITER)
        result.append(rawNumber.substring(THIRD_BLOCK_START, THIRD_BLOCK_END))

        return result.toString()
    }

    /**
     * Возвращает номер телефона без кода страны и спец символов, {@see PHONE_NUMBER_ALLOWED_SYMBOLS}
     */
    private fun getRawPhoneNumber(number: CharSequence): CharSequence {
        var rawNumber = number.replace("[$PHONE_NUMBER_ALLOWED_SYMBOLS]".toRegex(), "")
        if (rawNumber.length != FULL_RAW_NUMBER_LENGTH || rawNumber.startsWith(PLUS_PREFIX)) {
            rawNumber.startingPrefix?.let { rawNumber = rawNumber.substring(it.length) }
        }
        return rawNumber
    }

    private val String.startingPrefix: String?
        get() = arrayOf(SEVEN_PREFIX, EIGHT_PREFIX, PLUS_SEVEN_PREFIX, PLUS_EIGHT_PREFIX).firstOrNull { startsWith(it) }

    private const val FULL_RAW_NUMBER_LENGTH = 10

    private const val DIGITS_DELIMITER = "-"
    private const val OPENED_BRACKET = "("
    private const val CLOSED_BRACKET = ")"
    private const val PLUS_PREFIX = "+"
    private const val SEVEN_PREFIX = "7"
    private const val EIGHT_PREFIX = "8"
    private const val PLUS_SEVEN_PREFIX = "$PLUS_PREFIX$SEVEN_PREFIX"
    private const val PLUS_EIGHT_PREFIX = "$PLUS_PREFIX$EIGHT_PREFIX"
    private const val PRETTY_PREFIX = "$PLUS_SEVEN_PREFIX $OPENED_BRACKET"
    private const val CODE_BLOCK_PRETTY_FINISH = "$CLOSED_BRACKET "
    private const val PHONE_NUMBER_ALLOWED_SYMBOLS = "$DIGITS_DELIMITER $OPENED_BRACKET$CLOSED_BRACKET"

    private const val CODE_BLOCK_START = 0
    private const val CODE_BLOCK_END = 3
    private const val FIRST_BLOCK_START = CODE_BLOCK_END
    private const val FIRST_BLOCK_END = 6
    private const val SECOND_BLOCK_START = FIRST_BLOCK_END
    private const val SECOND_BLOCK_END = 8
    private const val THIRD_BLOCK_START = SECOND_BLOCK_END
    private const val THIRD_BLOCK_END = 10

}