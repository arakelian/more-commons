/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arakelian.core.enums;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * ISO 4217 Currency
 *
 * @see <a href="http://www.xe.com/symbols.php">http://www.xe.com/symbols.php</a>
 */
public enum IsoCurrencyCode {
    AUD(IsoCountryCode.AU, "Australian Dollar", "AUD", "AUD", "AUD"), //
    BGN(IsoCountryCode.BG, "Bulgarian lev", "лв", "&#1083;&#1074;", "лв"), //
    BRL(IsoCountryCode.BR, "Brasilian real", "R$", "R$", "R$"), //
    CAD(IsoCountryCode.CA, "Canadian Dollar", "CAD", "CAD", "CAD"), //
    CHF(IsoCountryCode.CH, "Swiss Franc", "CHF", "CHF", "CHF"), //
    CNY(IsoCountryCode.CN, "Chinese Yuan", "¥", "&#165;", "¥"), //
    CZK(IsoCountryCode.CZ, "Czech Koruna", "CZK", "CZK", "CZK"), //
    DKK(IsoCountryCode.DK, "Danish Krone", "DKK", "DKK", "DKK"), //
    EUR(null, "Euro", "€", "&#8364;", "€"), //
    GBP(IsoCountryCode.GB, "British Pound", "£", "GBP", "£"), //
    HKD(IsoCountryCode.HK, "Hong Kong dollar", "$", "$", "$"), //
    HRK(IsoCountryCode.HR, "Croatian kuna", "kn", "kn", "kn"), //
    HUF(IsoCountryCode.HU, "Hungarian Forint", "HUF", "HUF", "HUF"), //
    IDR(IsoCountryCode.ID, "Indonesian rupiah", "Rp", "Rp", "Rp"), //
    ILS(IsoCountryCode.IL, "Israeli shekel", "₪", "&#8362;", "₪"), //
    INR(IsoCountryCode.IN, "Indian rupee", "₹", null, "₹"), //
    JPY(IsoCountryCode.JP, "Japanese Yen", "¥", "JPY", "¥"), //
    KRW(IsoCountryCode.KR, "South Korean won", "₩", null, "₩"), //
    MXN(IsoCountryCode.MX, "Mexican peso", "₱", null, "₱"), //
    MYR(IsoCountryCode.MY, "Malaysian ringgit", null, null, null), //
    NOK(IsoCountryCode.NO, "Norwegian Krone", "NOK", "NOK", "NOK"), //
    NZD(IsoCountryCode.NZ, "New Zealand dollar", "$", "$", "$"), //
    PHP(IsoCountryCode.PH, "New Zealand dollar", "₱", null, "₱"), //
    PLN(IsoCountryCode.PL, "Polish Zloty", "PLN", "PLN", "PLN"), //
    RON(IsoCountryCode.RO, "New Romanian leu", "lei", "lei", "lie"), //
    RUB(IsoCountryCode.RU, "Russian Ruble", "руб.", "руб.", "руб."), //
    SEK(IsoCountryCode.SE, "Swedish Krona", "SEK", "SEK", "SEK"), //
    SGD(IsoCountryCode.SG, "Singapore Dollar", "SGD", "SGD", "SGD"), //
    THB(IsoCountryCode.TH, "Thai baht", "฿", "&#3647;", "฿"), //
    TRY(IsoCountryCode.TR, "Turkish lira", "₺", "&#8378;", "₺"), //
    USD(IsoCountryCode.US, "United States Dollar", "$", "&#36;", "$"), //
    ZAR(IsoCountryCode.ZA, "South African rand", "R", "R", "R"); //

    private static final long serialVersionUID = 1L;

    private static transient final Map<IsoCountryCode, IsoCurrencyCode> COUNTRIES;

    private static transient final Map<String, IsoCurrencyCode> CODES;

    static {
        final Map<IsoCountryCode, IsoCurrencyCode> countries = new HashMap<>();
        final Map<String, IsoCurrencyCode> names = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        for (final IsoCurrencyCode currency : IsoCurrencyCode.values()) {
            names.put(currency.name(), currency);
            final IsoCountryCode country = currency.getCountry();
            if (country != null && !countries.containsKey(country)) {
                countries.put(country, currency);
            }
        }
        COUNTRIES = Collections.unmodifiableMap(countries);
        // code map is case-insensitive
        CODES = Collections.unmodifiableMap(names);
    }

    /**
     * Returns {@link IsoCurrencyCode} for given country.
     *
     * @param country
     *            country
     * @return {@link IsoCurrencyCode} instance
     */
    public static IsoCurrencyCode fromCountry(final IsoCountryCode country) {
        if (country != null) {
            return COUNTRIES.get(country);
        }
        return null;
    }

    /**
     * Returns {@link IsoCurrencyCode} for given locale.
     *
     * @param locale
     *            locale
     * @return {@link IsoCurrencyCode} instance
     */
    public static IsoCurrencyCode fromLocaleCountry(final Locale locale) {
        final IsoCountryCode country = IsoCountryCode.fromLocaleCountry(locale);
        return fromCountry(country);
    }

    /**
     * Returns {@link IsoCurrencyCode} for given ISO 4217 identifier (case-insensitive).
     *
     * @param currencyCode
     *            ISO 4217 currency code
     * @return {@link IsoCurrencyCode} instance
     */
    public static IsoCurrencyCode fromString(final String currencyCode) {
        if (currencyCode == null || currencyCode.length() == 0) {
            return null;
        }
        // codes is case-insensitive
        return CODES.get(currencyCode);
    }

    private final IsoCountryCode country;
    private final String fullName;
    private final String symbol;
    private final String html;

    private final String abbreviation;

    private IsoCurrencyCode(
            final IsoCountryCode country,
            final String fullName,
            final String symbol,
            final String html,
            final String abbreviation) {
        this.country = country;
        this.fullName = fullName;
        this.symbol = symbol;
        this.html = html;
        this.abbreviation = abbreviation;
    }

    public NumberFormat createNumberFormat() {
        final Locale locale = country == IsoCountryCode.GB ? Locale.UK : Locale.US;
        final NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        format.setRoundingMode(RoundingMode.HALF_EVEN);
        return format;
    }

    public String getAbbreviation() {
        return this.abbreviation;
    }

    public IsoCountryCode getCountry() {
        return country;
    }

    public String getFullName() {
        return this.fullName;
    }

    public String getHtml() {
        return this.html;
    }

    public String getResourceBundleKey() {
        return getClass().getName() + "." + name();
    }

    public String getSymbol() {
        return this.symbol;
    }
}
