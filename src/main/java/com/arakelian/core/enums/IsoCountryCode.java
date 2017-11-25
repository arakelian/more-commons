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

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableSortedSet;

/**
 * Two character ISO 3166-1 country codes, along with a friendly label, and two boolean flags. The
 * first boolean flag indicates if the country is part of the EU, and the second boolean flag
 * indicates if they use the Euro.
 *
 * Note: The country names that are embedded in this enumeration don't "have" to be the ones
 * displayed to an end user. They are included here primarily as internal display purposes as well
 * as country detection.
 *
 * For postal code patterns, see: http://i18napis.appspot.com/address/data/&lt;country code&gt;
 *
 * @see IsoCurrencyCode
 * @see <a href=
 *      "https://github.com/googlei18n/libaddressinput">https://github.com/googlei18n/libaddressinput</a>
 * @see <a href=
 *      "https://github.com/googlei18n/libaddressinput/wiki/AddressValidationMetadata">https://github.com/googlei18n/libaddressinput/wiki/AddressValidationMetadata</a>
 */
public enum IsoCountryCode {
    // @formatter:off
    AF("Afghanistan", false, false, "\\d{4}", null, null, null, null), //
    AX("Åland Islands", false, false, "22\\d{3}", "%O%n%N%n%1%n%2%nAX-%Z %C%nÅLAND%n%I", null, null, null), //
    AL("Albania", false, false, "\\d{4}", null, null, null, null), //
    DZ("Algeria", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    AS("American Samoa", false, false, "(96799)(?:[ \\-](\\d{4}))?", "%N%n%O%n%1%n%2%n%C %S %Z%n%I", null, null, null), //
    AD("Andorra", false, false, "AD[1-7]0\\d", "%N%n%O%n%1%n%2%n%Z %C%n%I",
        new String[] { "Parròquia d'Andorra la Vella","Canillo","Encamp","Escaldes-Engordany","La Massana","Ordino","Sant Julià de Lòria" },
        new String[] { "Andorra la Vella","Canillo","Encamp","Escaldes-Engordany","La Massana","Ordino","Sant Julià de Lòria" }, null), //
    AO("Angola", false, false, null, null, null, null, null), //
    AI("Anguilla", false, false, "2640", null, null, null, null), //
    AQ("Antarctica", false, false, null, null, null, null, null), //
    AG("Antigua And Barbuda", false, false, null, null, null, null, null), //
    AR("Argentina", false, false, "((?:[A-HJ-NP-Z])?\\d{4})([A-Z]{3})?", "%N%n%O%n%1%n%2%n%Z %C%n%S%n%I",
        new String[] { "Buenos Aires","Catamarca","Chaco","Chubut","Ciudad Autónoma de Buenos Aires","Córdoba","Corrientes","Entre Ríos","Formosa","Jujuy","La Pampa","La Rioja","Mendoza","Misiones","Neuquén","Río Negro","Salta","San Juan","San Luis","Santa Cruz","Santa Fe","Santiago del Estero","Tierra del Fuego","Tucumán" },
        new String[] { "Buenos Aires","Catamarca","Chaco","Chubut","Ciudad de Buenos Aires","Córdoba","Corrientes","Entre Ríos","Formosa","Jujuy","La Pampa","La Rioja","Mendoza","Misiones","Neuquén","Río Negro","Salta","San Juan","San Luis","Santa Cruz","Santa Fe","Santiago del Estero","Tierra del Fuego","Tucumán" }, null), //
    AM("Armenia", false, false, "(37)?\\d{4}", "%N%n%O%n%1%n%2%n%Z%n%C%n%S%n%I",
        new String[] { "Արագածոտն","Արարատ","Արմավիր","Գեղարքունիք","Երևան","Լոռի","Կոտայք","Շիրակ","Սյունիք","Վայոց ձոր","Տավուշ" }, null,
        new String[] { "Aragatsotn","Ararat","Armavir","Gegharkunik","Yerevan","Lori","Kotayk","Shirak","Syunik","Vayots Dzor","Tavush" }), //
    AW("Aruba", false, false, null, null, null, null, null), //
    AU("Australia", false, false, "\\d{4}", "%O%n%N%n%1%n%2%n%C %S %Z%n%I",
        new String[] { "ACT","NSW","NT","QLD","SA","TAS","VIC","WA" },
        new String[] { "Australian Capital Territory","New South Wales","Northern Territory","Queensland","South Australia","Tasmania","Victoria","Western Australia" }, null), //
    AT("Austria", true, true, "\\d{4}", "%O%n%N%n%1%n%2%n%Z %C%n%I", null, null, null), //
    AZ("Azerbaijan", false, false, "\\d{4}", "%N%n%O%n%1%n%2%nAZ %Z %C%n%I", null, null, null), //
    BS("Bahamas", false, false, null, "%N%n%O%n%1%n%2%n%C, %S%n%I",
        new String[] { "ABACO","ACKLINS","ANDROS","BERRY ISLANDS","BIMINI","CAT ISLAND","CROOKED ISLAND","ELEUTHERA","EXUMA","GRAND BAHAMA","HARBOUR ISLAND","INAGUA","LONG ISLAND","MAYAGUANA","N.P.","RAGGED ISLAND","RUM CAY","SAN SALVADOR","SPANISH WELLS" },
        new String[] { "Abaco Islands","Acklins","Andros Island","Berry Islands","Bimini","Cat Island","Crooked Island","Eleuthera","Exuma and Cays","Grand Bahama","Harbour Island","Inagua","Long Island","Mayaguana","New Providence","Ragged Island","Rum Cay","San Salvador","Spanish Wells" }, null), //
    BH("Bahrain", false, false, "(?:(?:\\d|1[0-2])\\d{2})?", "%N%n%O%n%1%n%2%n%C %Z%n%I", null, null, null), //
    BD("Bangladesh", false, false, "\\d{4}", "%N%n%O%n%1%n%2%n%C - %Z%n%I", null, null, null), //
    BB("Barbados", false, false, "(?:BB\\d{5})?", null, null, null, null), //
    BY("Belarus", false, false, "\\d{6}", "%S%n%Z %C %X%n%1%n%2%n%O%n%N%n%I", null, null, null), //
    BE("Belgium", true, true, "\\d{4}", "%O%n%N%n%1%n%2%n%Z %C%n%I", null, null, null), //
    BZ("Belize", false, false, null, null, null, null, null), //
    BJ("Benin", false, false, null, null, null, null, null), //
    BM("Bermuda", false, false, "[A-Z]{2}[ ]?[A-Z0-9]{2}", "%N%n%O%n%1%n%2%n%C %Z%n%I", null, null, null), //
    BT("Bhutan", false, false, "\\d{5}", null, null, null, null), //
    BO("Bolivia, Plurinational State Of", false, false, null, null, null, null, null), //
    BQ("Bonaire, Sint Eustatius And Saba", false, false, null, null, null, null, null), //
    BA("Bosnia And Herzegovina", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    BW("Botswana", false, false, null, null, null, null, null), //
    BV("Bouvet Island", false, false, null, null, null, null, null), //
    BR("Brazil", false, false, "\\d{5}[\\-]?\\d{3}", "%O%n%N%n%1%n%2%n%D%n%C-%S%n%Z%n%I",
        new String[] { "AC","AL","AP","AM","BA","CE","DF","ES","GO","MA","MT","MS","MG","PA","PB","PR","PE","PI","RJ","RN","RS","RO","RR","SC","SP","SE","TO" },
        new String[] { "Acre","Alagoas","Amapá","Amazonas","Bahia","Ceará","Distrito Federal","Espírito Santo","Goiás","Maranhão","Mato Grosso","Mato Grosso do Sul","Minas Gerais","Pará","Paraíba","Paraná","Pernambuco","Piauí","Rio de Janeiro","Rio Grande do Norte","Rio Grande do Sul","Rondônia","Roraima","Santa Catarina","São Paulo","Sergipe","Tocantins" }, null), //
    IO("British Indian Ocean Territory", false, false, "BBND 1ZZ", "%N%n%O%n%1%n%2%n%X%n%C%n%Z%n%I", null, null, null), //
    BN("Brunei Darussalam", false, false, "[A-Z]{2}[ ]?\\d{4}", "%N%n%O%n%1%n%2%n%C %Z%n%I", null, null, null), //
    BG("Bulgaria", true, false, "\\d{4}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    BF("Burkina Faso", false, false, null, "%N%n%O%n%1%n%2%n%C %X%n%I", null, null, null), //
    BI("Burundi", false, false, null, null, null, null, null), //
    KH("Cambodia", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%C %Z%n%I", null, null, null), //
    CM("Cameroon", false, false, null, null, null, null, null), //
    CA("Canada", false, false, "[ABCEGHJKLMNPRSTVXY]\\d[ABCEGHJ-NPRSTV-Z][ ]?\\d[ABCEGHJ-NPRSTV-Z]\\d", "%N%n%O%n%1%n%2%n%C %S %Z%n%I",
        new String[] { "AB","BC","MB","NB","NL","NT","NS","NU","ON","PE","QC","SK","YT" },
        new String[] { "Alberta","British Columbia","Manitoba","New Brunswick","Newfoundland and Labrador","Northwest Territories","Nova Scotia","Nunavut","Ontario","Prince Edward Island","Quebec","Saskatchewan","Yukon" }, null), //
    CV("Cape Verde", false, false, "\\d{4}", "%N%n%O%n%1%n%2%n%Z %C%n%S%n%I",
        new String[] { "Boa Vista","Brava","Fogo","Maio","Sal","Santiago","Santo Antão","São Nicolau","São Vicente" }, null, null), //
    KY("Cayman Islands", false, false, "KY\\d-\\d{4}", "%N%n%O%n%1%n%2%n%S %Z%n%I",
        new String[] { "CAYMAN BRAC","GRAND CAYMAN","LITTLE CAYMAN" }, null, null), //
    CF("Central African Republic", false, false, null, null, null, null, null), //
    TD("Chad", false, false, null, null, null, null, null), //
    CL("Chile", false, false, "\\d{7}", "%N%n%O%n%1%n%2%n%Z %C%n%S%n%I",
        new String[] { "Antofagasta","Araucanía","Arica y Parinacota","Atacama","Aysén","Biobío","Coquimbo","O'Higgins","Los Lagos","Los Ríos","Magallanes","Maule","Región Metropolitana","Tarapacá","Valparaíso" },
        new String[] { "Antofagasta","Araucanía","Arica y Parinacota","Atacama","Aysén del General Carlos Ibáñez del Campo","Biobío","Coquimbo","Libertador General Bernardo O'Higgins","Los Lagos","Los Ríos","Magallanes y de la Antártica Chilena","Maule","Metropolitana de Santiago","Tarapacá","Valparaíso" }, null), //
    CN("China", false, false, "\\d{6}", "%Z%n%S%C%D%n%1%n%2%n%O%n%N%n%I",
        new String[] { "安徽省","澳门","北京市","重庆市","福建省","甘肃省","广东省","广西壮族自治区","贵州省","海南省","河北省","河南省","黑龙江省","湖北省","湖南省","吉林省","江苏省","江西省","辽宁省","内蒙古自治区","宁夏回族自治区","青海省","山东省","山西省","陕西省","上海市","四川省","台湾","天津市","西藏自治区","香港","新疆维吾尔自治区","云南省","浙江省" },
        new String[] { "安徽省","澳门","北京市","重庆市","福建省","甘肃省","广东省","广西","贵州省","海南省","河北省","河南省","黑龙江省","湖北省","湖南省","吉林省","江苏省","江西省","辽宁省","内蒙古","宁夏","青海省","山东省","山西省","陕西省","上海市","四川省","台湾","天津市","西藏","香港","新疆","云南省","浙江省" },
        new String[] { "Anhui Sheng","Macau","Beijing Shi","Chongqing Shi","Fujian Sheng","Gansu Sheng","Guangdong Sheng","Guangxi Zhuangzuzizhiqu","Guizhou Sheng","Hainan Sheng","Hebei Sheng","Henan Sheng","Heilongjiang Sheng","Hubei Sheng","Hunan Sheng","Jilin Sheng","Jiangsu Sheng","Jiangxi Sheng","Liaoning Sheng","Neimenggu Zizhiqu","Ningxia Huizuzizhiqu","Qinghai Sheng","Shandong Sheng","Shanxi Sheng","Shaanxi Sheng","Shanghai Shi","Sichuan Sheng","Taiwan","Tianjin Shi","Xizang Zizhiqu","Hong Kong","Xinjiang Weiwuerzizhiqu","Yunnan Sheng","Zhejiang Sheng" }), //
    CX("Christmas Island", false, false, "6798", "%O%n%N%n%1%n%2%n%C %S %Z%n%I", null, null, null), //
    CC("Cocos (keeling) Islands", false, false, "6799", "%O%n%N%n%1%n%2%n%C %S %Z%n%I", null, null, null), //
    CO("Colombia", false, false, "\\d{6}", "%N%n%O%n%1%n%2%n%C, %S%n%I", null, null, null), //
    KM("Comoros", false, false, null, null, null, null, null), //
    CG("Congo", false, false, null, null, null, null, null), //
    CD("Congo, The Democratic Republic Of The", false, false, null, "%N%n%O%n%1%n%2%n%C %X%n%I", null, null, null), //
    CK("Cook Islands", false, false, null, null, null, null, null), //
    CR("Costa Rica", false, false, "\\d{4,5}|\\d{3}-\\d{4}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    CI("Côte D'ivoire", false, false, null, "%N%n%O%n%X %1 %2 %C %X%n%I", null, null, null), //
    HR("Croatia", true, false, "\\d{5}", "%N%n%O%n%1%n%2%nHR-%Z %C%n%I", null, null, null), //
    CU("Cuba", false, false, null, null, null, null, null), //
    CW("Curaçao", false, false, null, null, null, null, null), //
    CY("Cyprus", true, true, "\\d{4}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    CZ("Czech Republic", true, false, "\\d{3}[ ]?\\d{2}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    DK("Denmark", true, false, "\\d{4}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    DJ("Djibouti", false, false, null, null, null, null, null), //
    DM("Dominica", false, false, null, null, null, null, null), //
    DO("Dominican Republic", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    EC("Ecuador", false, false, "(?:[A-Z]\\d{4}[A-Z]|(?:[A-Z]{2})?\\d{6})?", "%N%n%O%n%1%n%2%n%Z%n%C%n%I", null, null, null), //
    EG("Egypt", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%C%n%S%n%Z%n%I",
        new String[] { "أسوان","أسيوط","الإسكندرية","الإسماعيلية","الأقصر","البحر الأحمر","البحيرة","الجيزة","الدقهلية","السويس","الشرقية","الغربية","الفيوم","القاهرة","القليوبية","المنوفية","المنيا","الوادي الجديد","بني سويف","بورسعيد","جنوب سيناء","دمياط","سوهاج","شمال سيناء","قنا","كفر الشيخ","مطروح" }, null,
        new String[] { "Aswan Governorate","Asyut Governorate","Alexandria Governorate","Ismailia Governorate","Luxor Governorate","Red Sea Governorate","El Beheira Governorate","Giza Governorate","Dakahlia Governorate","Suez Governorate","Ash Sharqia Governorate","Gharbia Governorate","Faiyum Governorate","Cairo Governorate","Qalyubia Governorate","Menofia Governorate","Menia Governorate","New Valley Governorate","Beni Suef Governorate","Port Said Governorate","South Sinai Governorate","Damietta Governorate","Sohag Governorate","North Sinai Governorate","Qena Governorate","Kafr El Sheikh Governorate","Matruh Governorate" }), //
    SV("El Salvador", false, false, "CP [1-3][1-7][0-2]\\d", "%N%n%O%n%1%n%2%n%Z-%C%n%S%n%I",
        new String[] { "AHUACHAPAN","CABANAS","CALATENANGO","CUSCATLAN","LA LIBERTAD","LA PAZ","LA UNION","MORAZAN","SAN MIGUEL","SAN SALVADOR","SAN VICENTE","SANTA ANA","SONSONATE","USULUTAN" },
        new String[] { "Ahuachapán","Cabañas","Chalatenango","Cuscatlán","La Libertad","La Paz","La Unión","Morazán","San Miguel","San Salvador","San Vicente","Santa Ana","Sonsonate","Usulután" }, null), //
    GQ("Equatorial Guinea", false, false, null, null, null, null, null), //
    ER("Eritrea", false, false, null, null, null, null, null), //
    EE("Estonia", true, true, "\\d{5}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    ET("Ethiopia", false, false, "\\d{4}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    FK("Falkland Islands (malvinas)", false, false, "FIQQ 1ZZ", "%N%n%O%n%1%n%2%n%X%n%C%n%Z%n%I", null, null, null), //
    FO("Faroe Islands", false, false, "\\d{3}", "%N%n%O%n%1%n%2%nFO%Z %C%n%I", null, null, null), //
    FJ("Fiji", false, false, null, null, null, null, null), //
    FI("Finland", true, true, "\\d{5}", "%O%n%N%n%1%n%2%nFI-%Z %C%n%I", null, null, null), //
    FR("France", true, true, "\\d{2}[ ]?\\d{3}", "%O%n%N%n%1%n%2%n%Z %C %X%n%I", null, null, null), //
    GF("French Guiana", false, false, "9[78]3\\d{2}", "%O%n%N%n%1%n%2%n%Z %C %X%n%I", null, null, null), //
    PF("French Polynesia", false, false, "987\\d{2}", "%N%n%O%n%1%n%2%n%Z %C %S%n%I", null, null, null), //
    TF("French Southern Territories", false, false, null, null, null, null, null), //
    GA("Gabon", false, false, null, null, null, null, null), //
    GM("Gambia", false, false, null, null, null, null, null), //
    GE("Georgia", false, false, "\\d{4}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    DE("Germany", true, true, "\\d{5}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    GH("Ghana", false, false, null, null, null, null, null), //
    GI("Gibraltar", false, false, "GX11 1AA", "%N%n%O%n%1%n%2%nGIBRALTAR%n%Z%n%I", null, null, null), //
    GR("Greece", true, true, "\\d{3} ?\\d{2}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    GL("Greenland", false, false, "39\\d{2}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    GD("Grenada", false, false, null, null, null, null, null), //
    GP("Guadeloupe", false, false, "9[78][01]\\d{2}", "%O%n%N%n%1%n%2%n%Z %C %X%n%I", null, null, null), //
    GU("Guam", false, false, "(969(?:[12]\\d|3[12]))(?:[ \\-](\\d{4}))?", "%N%n%O%n%1%n%2%n%C %S %Z%n%I", null, null, null), //
    GT("Guatemala", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%Z- %C%n%I", null, null, null), //
    GG("Guernsey", false, false, "GY\\d[\\dA-Z]?[ ]?\\d[ABD-HJLN-UW-Z]{2}", "%N%n%O%n%1%n%2%n%X%n%C%nGUERNSEY%n%Z%n%I", null, null, null), //
    GN("Guinea", false, false, "\\d{3}", "%N%n%O%n%Z %1 %2 %C%n%I", null, null, null), //
    GW("Guinea-bissau", false, false, "\\d{4}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    GY("Guyana", false, false, null, null, null, null, null), //
    HT("Haiti", false, false, "\\d{4}", "%N%n%O%n%1%n%2%nHT%Z %C %X%n%I", null, null, null), //
    HM("Heard Island And Mcdonald Islands", false, false, "\\d{4}", "%O%n%N%n%1%n%2%n%C %S %Z%n%I", null, null, null), //
    VA("Holy See (vatican City State)", false, false, "00120", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    HN("Honduras", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%C, %S%n%Z%n%I", null, null, null), //
    HK("Hong Kong", false, false, null, "%S%n%C%n%1%n%2%n%O%n%N%n%I",
        new String[] { "九龍","香港島","新界" }, null,
        new String[] { "Kowloon","Hong Kong Island","New Territories" }), //
    HU("Hungary", true, false, "\\d{4}", "%N%n%O%n%C%n%1%n%2%n%Z%n%I", null, null, null), //
    IS("Iceland", false, false, "\\d{3}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    IN("India", false, false, "\\d{6}", "%N%n%O%n%1%n%2%n%C %Z%n%S%n%I",
        new String[] { "Andaman and Nicobar Islands","Andhra Pradesh","Arunachal Pradesh","Assam","Bihar","Chandigarh","Chhattisgarh","Dadra and Nagar Haveli","Daman and Diu","Delhi","Goa","Gujarat","Haryana","Himachal Pradesh","Jammu and Kashmir","Jharkhand","Karnataka","Kerala","Lakshadweep","Madhya Pradesh","Maharashtra","Manipur","Meghalaya","Mizoram","Nagaland","Odisha","Puducherry","Punjab","Rajasthan","Sikkim","Tamil Nadu","Telangana","Tripura","Uttar Pradesh","Uttarakhand","West Bengal" },
        new String[] { "Andaman & Nicobar","Andhra Pradesh","Arunachal Pradesh","Assam","Bihar","Chandigarh","Chhattisgarh","Dadra & Nagar Haveli","Daman & Diu","Delhi","Goa","Gujarat","Haryana","Himachal Pradesh","Jammu & Kashmir","Jharkhand","Karnataka","Kerala","Lakshadweep","Madhya Pradesh","Maharashtra","Manipur","Meghalaya","Mizoram","Nagaland","Odisha","Puducherry","Punjab","Rajasthan","Sikkim","Tamil Nadu","Telangana","Tripura","Uttar Pradesh","Uttarakhand","West Bengal" }, null), //
    ID("Indonesia", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%C%n%S %Z%n%I",
        new String[] { "Aceh","Bali","Banten","Bengkulu","Daerah Istimewa Yogyakarta","DKI Jakarta","Gorontalo","Jambi","Jawa Barat","Jawa Tengah","Jawa Timur","Kalimantan Barat","Kalimantan Selatan","Kalimantan Tengah","Kalimantan Timur","Kepulauan Bangka Belitung","Kepulauan Riau","Lampung","Maluku","Maluku Utara","Nusa Tenggara Barat","Nusa Tenggara Timur","Papua","Papua Barat","Riau","Sulawesi Barat","Sulawesi Selatan","Sulawesi Tengah","Sulawesi Tenggara","Sulawesi Utara","Sumatera Barat","Sumatera Selatan","Sumatera Utara" }, null, null), //
    IR("Iran, Islamic Republic Of", false, false, "\\d{5}-?\\d{5}", "%O%n%N%n%S%n%C, %D%n%1%n%2%n%Z%n%I", null, null, null), //
    IQ("Iraq", false, false, "\\d{5}", "%O%n%N%n%1%n%2%n%C, %S%n%Z%n%I", null, null, null), //
    IE("Ireland", true, true, null, "%N%n%O%n%1%n%2%n%C%n%S%n%I",
        new String[] { "Co. Carlow","Co. Cavan","Co. Clare","Co. Cork","Co. Donegal","Co. Dublin","Co. Galway","Co. Kerry","Co. Kildare","Co. Kilkenny","Co. Laois","Co. Leitrim","Co. Limerick","Co. Longford","Co. Louth","Co. Mayo","Co. Meath","Co. Monaghan","Co. Offaly","Co. Roscommon","Co. Sligo","Co. Tipperary","Co. Waterford","Co. Westmeath","Co. Wexford","Co. Wicklow" }, null, null), //
    IM("Isle Of Man", false, false, "IM\\d[\\dA-Z]?[ ]?\\d[ABD-HJLN-UW-Z]{2}", "%N%n%O%n%1%n%2%n%X%n%C%n%Z%n%I", null, null, null), //
    IL("Israel", false, false, "\\d{5}(?:\\d{2})?", "%N%n%O%n%1%n%2%n%C %Z%n%I", null, null, null), //
    IT("Italy", true, true, "\\d{5}", "%N%n%O%n%1%n%2%n%Z %C %S%n%I",
        new String[] { "AG","AL","AN","AR","AP","AT","AV","BA","BT","BL","BN","BG","BI","BO","BZ","BS","BR","CA","CL","CB","CI","CE","CT","CZ","CH","CO","CS","CR","KR","CN","EN","FM","FE","FI","FG","FC","FR","GE","GO","GR","IM","IS","AQ","SP","LT","LE","LC","LI","LO","LU","MC","MN","MS","MT","VS","ME","MI","MO","MB","NA","NO","NU","OG","OT","OR","PD","PA","PR","PV","PG","PU","PE","PC","PI","PT","PN","PZ","PO","RG","RA","RC","RE","RI","RN","RM","RO","SA","SS","SV","SI","SR","SO","TA","TE","TR","TO","TP","TN","TV","TS","UD","AO","VA","VE","VB","VC","VR","VV","VI","VT" },
        new String[] { "Agrigento","Alessandria","Ancona","Arezzo","Ascoli Piceno","Asti","Avellino","Bari","Barletta-Andria-Trani","Belluno","Benevento","Bergamo","Biella","Bologna","Bolzano","Brescia","Brindisi","Cagliari","Caltanissetta","Campobasso","Carbonia-Iglesias","Caserta","Catania","Catanzaro","Chieti","Como","Cosenza","Cremona","Crotone","Cuneo","Enna","Fermo","Ferrara","Firenze","Foggia","Forlì-Cesena","Frosinone","Genova","Gorizia","Grosseto","Imperia","Isernia","L'Aquila","La Spezia","Latina","Lecce","Lecco","Livorno","Lodi","Lucca","Macerata","Mantova","Massa-Carrara","Matera","Medio Campidano","Messina","Milano","Modena","Monza e della Brianza","Napoli","Novara","Nuoro","Ogliastra","Olbia-Tempio","Oristano","Padova","Palermo","Parma","Pavia","Perugia","Pesaro e Urbino","Pescara","Piacenza","Pisa","Pistoia","Pordenone","Potenza","Prato","Ragusa","Ravenna","Reggio Calabria","Reggio nell'Emilia","Rieti","Rimini","Roma","Rovigo","Salerno","Sassari","Savona","Siena","Siracusa","Sondrio","Taranto","Teramo","Terni","Torino","Trapani","Trento","Treviso","Trieste","Udine","Valle d'Aosta","Varese","Venezia","Verbano-Cusio-Ossola","Vercelli","Verona","Vibo Valentia","Vicenza","Viterbo" }, null), //
    JM("Jamaica", false, false, null, "%N%n%O%n%1%n%2%n%C%n%S %X%n%I",
        new String[] { "Clarendon","Hanover","Kingston","Manchester","Portland","St. Andrew","St. Ann","St. Catherine","St. Elizabeth","St. James","St. Mary","St. Thomas","Trelawny","Westmoreland" }, null, null), //
    JP("Japan", false, false, "\\d{3}-?\\d{4}", "〒%Z%n%S%C%n%1%n%2%n%O%n%N%n%I",
        new String[] { "北海道","青森県","岩手県","宮城県","秋田県","山形県","福島県","茨城県","栃木県","群馬県","埼玉県","千葉県","東京都","神奈川県","新潟県","富山県","石川県","福井県","山梨県","長野県","岐阜県","静岡県","愛知県","三重県","滋賀県","京都府","大阪府","兵庫県","奈良県","和歌山県","鳥取県","島根県","岡山県","広島県","山口県","徳島県","香川県","愛媛県","高知県","福岡県","佐賀県","長崎県","熊本県","大分県","宮崎県","鹿児島県","沖縄県" }, null,
        new String[] { "Hokkaido","Aomori","Iwate","Miyagi","Akita","Yamagata","Fukushima","Ibaraki","Tochigi","Gunma","Saitama","Chiba","Tokyo","Kanagawa","Niigata","Toyama","Ishikawa","Fukui","Yamanashi","Nagano","Gifu","Shizuoka","Aichi","Mie","Shiga","Kyoto","Osaka","Hyogo","Nara","Wakayama","Tottori","Shimane","Okayama","Hiroshima","Yamaguchi","Tokushima","Kagawa","Ehime","Kochi","Fukuoka","Saga","Nagasaki","Kumamoto","Oita","Miyazaki","Kagoshima","Okinawa" }), //
    JE("Jersey", false, false, "JE\\d[\\dA-Z]?[ ]?\\d[ABD-HJLN-UW-Z]{2}", "%N%n%O%n%1%n%2%n%X%n%C%nJERSEY%n%Z%n%I", null, null, null), //
    JO("Jordan", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%C %Z%n%I", null, null, null), //
    KZ("Kazakhstan", false, false, "\\d{6}", "%Z%n%S%n%C%n%1%n%2%n%O%n%N%n%I", null, null, null), //
    KE("Kenya", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%C%n%Z%n%I", null, null, null), //
    KI("Kiribati", false, false, null, "%N%n%O%n%1%n%2%n%S%n%C%n%I", null, null, null), //
    KP("Korea, Democratic People's Republic Of", false, false, null, null, null, null, null), //
    KR("Korea, Republic Of", false, false, "\\d{3}[\\-]\\d{3}", "%S %C%D%n%1%n%2%n%O%n%N%n%Z%n%I",
        new String[] { "강원도","경기도","경상남도","경상북도","광주광역시","대구광역시","대전광역시","부산광역시","서울특별시","세종특별자치시","울산광역시","인천광역시","전라남도","전라북도","제주특별자치도","충청남도","충청북도" },
        new String[] { "강원","경기","경남","경북","광주","대구","대전","부산","서울","세종","울산","인천","전남","전북","제주","충남","충북" },
        new String[] { "Gangwon-do","Gyeonggi-do","Gyeongsangnam-do","Gyeongsangbuk-do","Gwangju","Daegu","Daejeon","Busan","Seoul","Sejong","Ulsan","Incheon","Jeollanam-do","Jeollabuk-do","Jeju-do","Chungcheongnam-do","Chungcheongbuk-do" }), //
    KW("Kuwait", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    KG("Kyrgyzstan", false, false, "\\d{6}", "%Z %C %X%n%1%n%2%n%O%n%N%n%I", null, null, null), //
    LA("Lao People's Democratic Republic", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    LV("Latvia", true, true, "LV-\\d{4}", "%N%n%O%n%1%n%2%n%C, %Z%n%I", null, null, null), //
    LB("Lebanon", false, false, "(?:(?:\\d{4})(?:[ ]?(?:\\d{4}))?)?", "%N%n%O%n%1%n%2%n%C %Z%n%I", null, null, null), //
    LS("Lesotho", false, false, "\\d{3}", "%N%n%O%n%1%n%2%n%C %Z%n%I", null, null, null), //
    LR("Liberia", false, false, "\\d{4}", "%N%n%O%n%1%n%2%n%Z %C %X%n%I", null, null, null), //
    LY("Libya", false, false, null, null, null, null, null), //
    LI("Liechtenstein", false, false, "(948[5-9])|(949[0-7])", "%O%n%N%n%1%n%2%nFL-%Z %C%n%I", null, null, null), //
    LT("Lithuania", true, false, "\\d{5}", "%O%n%N%n%1%n%2%nLT-%Z %C%n%I", null, null, null), //
    LU("Luxembourg", true, true, "\\d{4}", "%O%n%N%n%1%n%2%nL-%Z %C%n%I", null, null, null), //
    MO("Macao", false, false, null, "%1%n%2%n%O%n%N%n%I", null, null, null), //
    MK("Macedonia, The Former Yugoslav Republic Of", false, false, "\\d{4}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    MG("Madagascar", false, false, "\\d{3}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    MW("Malawi", false, false, null, "%N%n%O%n%1%n%2%n%C %X%n%I", null, null, null), //
    MY("Malaysia", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%D%n%Z %C%n%S%n%I",
        new String[] { "JOHOR","KEDAH","KELANTAN","KUALA LUMPUR","LABUAN","MELAKA","NEGERI SEMBILAN","PAHANG","PERAK","PERLIS","PULAU PINANG","PUTRAJAYA","SABAH","SARAWAK","SELANGOR","TERENGGANU" }, null, null), //
    MV("Maldives", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%C %Z%n%I", null, null, null), //
    ML("Mali", false, false, null, null, null, null, null), //
    MT("Malta", true, true, "[A-Z]{3}[ ]?\\d{2,4}", "%N%n%O%n%1%n%2%n%C %Z%n%I", null, null, null), //
    MH("Marshall Islands", false, false, "(969[67]\\d)(?:[ \\-](\\d{4}))?", "%N%n%O%n%1%n%2%n%C %S %Z%n%I", null, null, null), //
    MQ("Martinique", false, false, "9[78]2\\d{2}", "%O%n%N%n%1%n%2%n%Z %C %X%n%I", null, null, null), //
    MR("Mauritania", false, false, null, null, null, null, null), //
    MU("Mauritius", false, false, "(?:\\d{3}(?:\\d{2}|[A-Z]{2}\\d{3}))?", "%N%n%O%n%1%n%2%n%Z%n%C%n%I", null, null, null), //
    YT("Mayotte", false, false, "976\\d{2}", "%O%n%N%n%1%n%2%n%Z %C %X%n%I", null, null, null), //
    MX("Mexico", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%D%n%Z %C, %S%n%I",
        new String[] { "AGS","BC","BCS","CAMP","CHIS","CHIH","COAH","COL","D.F.","DGO","MEX","GTO","GRO","HGO","JAL","MICH","MOR","NAY","NL","OAX","PUE","QRO","QROO","SLP","SIN","SON","TAB","TAMPS","TLAX","VER","YUC","ZAC" },
        new String[] { "Aguascalientes","Baja California","Baja California Sur","Campeche","Chiapas","Chihuahua","Coahuila","Colima","Distrito Federal","Durango","Estado de México","Guanajuato","Guerrero","Hidalgo","Jalisco","Michoacán","Morelos","Nayarit","Nuevo León","Oaxaca","Puebla","Querétaro","Quintana Roo","San Luís Potosí","Sinalóa","Sonora","Tabasco","Tamaulipas","Tlaxcala","Veracruz","Yucatán","Zacatecas" }, null), //
    FM("Micronesia, Federated States Of", false, false, "(9694[1-4])(?:[ \\-](\\d{4}))?", "%N%n%O%n%1%n%2%n%C %S %Z%n%I", null, null, null), //
    MD("Moldova, Republic Of", false, false, "\\d{4}", "%N%n%O%n%1%n%2%nMD-%Z %C%n%I", null, null, null), //
    MC("Monaco", false, false, "980\\d{2}", "%N%n%O%n%1%n%2%nMC-%Z %C %X%n%I", null, null, null), //
    MN("Mongolia", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%S %C-%X%n%Z%n%I", null, null, null), //
    ME("Montenegro", false, false, "8\\d{4}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    MS("Montserrat", false, false, null, null, null, null, null), //
    MA("Morocco", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    MZ("Mozambique", false, false, "\\d{4}", null, null, null, null), //
    MM("Myanmar", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%C, %Z%n%I", null, null, null), //
    NA("Namibia", false, false, null, null, null, null, null), //
    NR("Nauru", false, false, null, "%N%n%O%n%1%n%2%n%S%n%I",
        new String[] { "AIWO DISTRICT","ANABAR DISTRICT","ANETAN DISTRICT","ANIBARE DISTRICT","BAITI DISTRICT","BOE DISTRICT","BUADA DISTRICT","DENIGOMODU DISTRICT","EWA DISTRICT","IJUW DISTRICT","MENENG DISTRICT","NIBOK DISTRICT","UABOE DISTRICT","YAREN DISTRICT" }, null, null), //
    NP("Nepal", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%C %Z%n%I", null, null, null), //
    NL("Netherlands", true, true, "\\d{4}[ ]?[A-Z]{2}", "%O%n%N%n%1%n%2%n%Z %C%n%I", null, null, null), //
    NC("New Caledonia", false, false, "988\\d{2}", "%O%n%N%n%1%n%2%n%Z %C %X%n%I", null, null, null), //
    NZ("New Zealand", false, false, "\\d{4}", "%N%n%O%n%1%n%2%n%D%n%C %Z%n%I", null, null, null), //
    NI("Nicaragua", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%Z%n%C, %S%n%I",
        new String[] { "Boaco","Carazo","Chinandega","Chontales","Esteli","Granada","Jinotega","Leon","Madriz","Managua","Masaya","Matagalpa","Nueva Segovia","Raan","Raas","Rio San Juan","Rivas" }, null, null), //
    NE("Niger", false, false, "\\d{4}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    NG("Nigeria", false, false, "(?:\\d{6})?", "%N%n%O%n%1%n%2%n%C %Z%n%S%n%I",
        new String[] { "Abia","Adamawa","Akwa Ibom","Anambra","Bauchi","Bayelsa","Benue","Borno","Cross River","Delta","Ebonyi","Edo","Ekiti","Enugu","Federal Capital Territory","Gombe","Imo","Jigawa","Kaduna","Kano","Katsina","Kebbi","Kogi","Kwara","Lagos","Nasarawa","Niger","Ogun State","Ondo","Osun","Oyo","Plateau","Rivers","Sokoto","Taraba","Yobe","Zamfara" }, null, null), //
    NU("Niue", false, false, null, null, null, null, null), //
    NF("Norfolk Island", false, false, "2899", "%O%n%N%n%1%n%2%n%C %S %Z%n%I", null, null, null), //
    MP("Northern Mariana Islands", false, false, "(9695[012])(?:[ \\-](\\d{4}))?", "%N%n%O%n%1%n%2%n%C %S %Z%n%I", null, null, null), //
    NO("Norway", false, false, "\\d{4}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    OM("Oman", false, false, "(PC )?\\d{3}", "%N%n%O%n%1%n%2%n%Z%n%C%n%I", null, null, null), //
    PK("Pakistan", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%C-%Z%n%I", null, null, null), //
    PW("Palau", false, false, "(969(?:39|40))(?:[ \\-](\\d{4}))?", "%N%n%O%n%1%n%2%n%C %S %Z%n%I", null, null, null), //
    PS("Palestine, State Of", false, false, null, null, null, null, null), //
    PA("Panama", false, false, null, "%N%n%O%n%1%n%2%n%C%n%S%n%I", null, null, null), //
    PG("Papua New Guinea", false, false, "\\d{3}", "%N%n%O%n%1%n%2%n%C %Z %S%n%I", null, null, null), //
    PY("Paraguay", false, false, "\\d{4}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    PE("Peru", false, false, "(?:LIMA \\d|CALLAO 0?)\\d|[0-2]\\d{4}", null, null, null, null), //
    PH("Philippines", false, false, "\\d{4}", "%N%n%O%n%1%n%2%n%D, %C%n%Z %S%n%I",
        new String[] { "Abra","Agusan del Norte","Agusan del Sur","Aklan","Albay","Antique","Apayao","Aurora","Basilan","Bataan","Batanes","Batangas","Benguet","Biliran","Bohol","Bukidnon","Bulacan","Cagayan","Camarines Norte","Camarines Sur","Camiguin","Capiz","Catanduanes","Cavite","Cebu","Compostela Valley","Cotabato","Davao del Norte","Davao del Sur","Davao Occidental","Davao Oriental","Dinagat Islands","Eastern Samar","Guimaras","Ifugao","Ilocos Norte","Ilocos Sur","Iloilo","Isabela","Kalinga","La Union","Laguna","Lanao del Norte","Lanao del Sur","Leyte","Maguindanao","Marinduque","Masbate","Metro Manila","Mindoro Occidental","Mindoro Oriental","Misamis Occidental","Misamis Oriental","Mountain Province","Negros Occidental","Negros Oriental","Northern Samar","Nueva Ecija","Nueva Vizcaya","Palawan","Pampanga","Pangasinan","Quezon Province","Quirino","Rizal","Romblon","Samar","Sarangani","Siquijor","Sorsogon","South Cotabato","Southern Leyte","Sultan Kudarat","Sulu","Surigao del Norte","Surigao del Sur","Tarlac","Tawi-Tawi","Zambales","Zamboanga del Norte","Zamboanga del Sur","Zamboanga Sibuguey" }, null, null), //
    PN("Pitcairn", false, false, "PCRN 1ZZ", "%N%n%O%n%1%n%2%n%X%n%C%n%Z%n%I", null, null, null), //
    PL("Poland", true, false, "\\d{2}-\\d{3}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    PT("Portugal", true, true, "\\d{4}-\\d{3}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    PR("Puerto Rico", false, false, "(00[679]\\d{2})(?:[ \\-](\\d{4}))?", "%N%n%O%n%1%n%2%n%C PR %Z%n%I", null, null, null), //
    QA("Qatar", false, false, null, null, null, null, null), //
    RE("Réunion", false, false, "9[78]4\\d{2}", "%O%n%N%n%1%n%2%n%Z %C %X%n%I", null, null, null), //
    RO("Romania", true, false, "\\d{6}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    RU("Russian Federation", false, false, "\\d{6}", "%N%n%O%n%1%n%2%n%C%n%S%n%Z%n%I",
        new String[] { "Алтайский край","Амурская область","Архангельская область","Астраханская область","Белгородская область","Брянская область","Владимирская область","Волгоградская область","Вологодская область","Воронежская область","Еврейская автономная область","Забайкальский край","Ивановская область","Иркутская область","Кабардино-Балкарская Республика","Калининградская область","Калужская область","Камчатский край","Карачаево-Черкесская Республика","Кемеровская область","Кировская область","Костромская область","Краснодарский край","Красноярский край","Курганская область","Курская область","Ленинградская область","Липецкая область","Магаданская область","город Москва","Московская область","Мурманская область","Ненецкий автономный округ","Нижегородская область","Новгородская область","Новосибирская область","Омская область","Оренбургская область","Орловская область","Пензенская область","Пермский край","Приморский край","Псковская область","Республика Адыгея","Республика Алтай","Республика Башкортостан","Республика Бурятия","Республика Дагестан","Республика Ингушетия","Республика Калмыкия","Республика Карелия","Республика Коми","Автономна Республіка Крим","Республика Марий Эл","Республика Мордовия","Республика Саха (Якутия)","Республика Северная Осетия-Алания","Республика Татарстан","Республика Тыва","Республика Удмуртия","Республика Хакасия","Ростовская область","Рязанская область","Самарская область","город Санкт-Петербург","Саратовская область","Сахалинская область","Свердловская область","город Севастополь","Смоленская область","Ставропольский край","Тамбовская область","Тверская область","Томская область","Тульская область","Тюменская область","Ульяновская область","Хабаровский край","Ханты-Мансийский автономный округ","Челябинская область","Чеченская Республика","Чувашская Республика","Чукотский автономный округ","Ямало-Ненецкий автономный округ","Ярославская область" },
        new String[] { "Алтайский край","Амурская область","Архангельская область","Астраханская область","Белгородская область","Брянская область","Владимирская область","Волгоградская область","Вологодская область","Воронежская область","Еврейская автономная область","Забайкальский край","Ивановская область","Иркутская область","Кабардино-Балкарская Республика","Калининградская область","Калужская область","Камчатский край","Карачаево-Черкесская Республика","Кемеровская область","Кировская область","Костромская область","Краснодарский край","Красноярский край","Курганская область","Курская область","Ленинградская область","Липецкая область","Магаданская область","Москва","Московская область","Мурманская область","Ненецкий автономный округ","Нижегородская область","Новгородская область","Новосибирская область","Омская область","Оренбургская область","Орловская область","Пензенская область","Пермский край","Приморский край","Псковская область","Республика Адыгея","Республика Алтай","Республика Башкортостан","Республика Бурятия","Республика Дагестан","Республика Ингушетия","Республика Калмыкия","Республика Карелия","Республика Коми","Республика Крым","Республика Марий Эл","Республика Мордовия","Республика Саха (Якутия)","Республика Северная Осетия-Алания","Республика Татарстан","Республика Тыва","Республика Удмуртия","Республика Хакасия","Ростовская область","Рязанская область","Самарская область","Санкт-Петербург","Саратовская область","Сахалинская область","Свердловская область","Севастополь","Смоленская область","Ставропольский край","Тамбовская область","Тверская область","Томская область","Тульская область","Тюменская область","Ульяновская область","Хабаровский край","Ханты-Мансийский автономный округ","Челябинская область","Чеченская Республика","Чувашская Республика","Чукотский автономный округ","Ямало-Ненецкий автономный округ","Ярославская область" },
        new String[] { "Altayskiy kray","Amurskaya oblast'","Arkhangelskaya oblast'","Astrakhanskaya oblast'","Belgorodskaya oblast'","Bryanskaya oblast'","Vladimirskaya oblast'","Volgogradskaya oblast'","Vologodskaya oblast'","Voronezhskaya oblast'","Evreyskaya avtonomnaya oblast'","Zabaykalskiy kray","Ivanovskaya oblast'","Irkutskaya oblast'","Kabardino-Balkarskaya Republits","Kaliningradskaya oblast'","Kaluzhskaya oblast'","Kamchatskiy kray","Karachaevo-Cherkesskaya Republits","Kemerovskaya oblast'","Kirovskaya oblast'","Kostromskaya oblast'","Krasnodarskiy kray","Krasnoyarskiy kray","Kurganskaya oblast'","Kurskaya oblast'","Leningradskaya oblast'","Lipetskaya oblast'","Magadanskaya oblast'","gorod Moskva","Moskovskaya oblast'","Murmanskaya oblast'","Nenetskiy","Nizhegorodskaya oblast'","Novgorodskaya oblast'","Novosibirskaya oblast'","Omskaya oblast'","Orenburgskaya oblast'","Orlovskaya oblast'","Penzenskaya oblast'","Permskiy kray","Primorskiy kray","Pskovskaya oblast'","Respublika Adygeya","Altay Republits","Bashkortostan Republits","Buryatiya Republits","Dagestan Republits","Ingushetiya Republits","Respublika Kalmykiya","Kareliya Republits","Komi Republits","Respublika Krym","Respublika Mariy El","Respublika Mordoviya","Sakha (Yakutiya) Republits","Respublika Severnaya Osetiya-Alaniya","Respublika Tatarstan","Tyva Republits","Respublika Udmurtiya","Khakasiya Republits","Rostovskaya oblast'","Ryazanskaya oblast'","Samarskaya oblast'","gorod Sankt-Peterburg","Saratovskaya oblast'","Sakhalinskaya oblast'","Sverdlovskaya oblast'","gorod Sevastopol'","Smolenskaya oblast'","Stavropolskiy kray","Tambovskaya oblast'","Tverskaya oblast'","Tomskaya oblast'","Tulskaya oblast'","Tyumenskaya oblast'","Ulyanovskaya oblast'","Khabarovskiy kray","Khanty-Mansiyskiy avtonomnyy okrug","Chelyabinskaya oblast'","Chechenskaya Republits","Chuvashia","Chukotskiy","Yamalo-Nenetskiy","Yaroslavskaya oblast'" }), //
    RW("Rwanda", false, false, null, null, null, null, null), //
    BL("Saint Barthélemy", false, false, "9[78][01]\\d{2}", "%O%n%N%n%1%n%2%n%Z %C %X%n%I", null, null, null), //
    SH("Saint Helena, Ascension And Tristan Da Cunha", false, false, "(ASCN|STHL) 1ZZ", "%N%n%O%n%1%n%2%n%X%n%C%n%Z%n%I", null, null, null), //
    KN("Saint Kitts And Nevis", false, false, null, "%N%n%O%n%1%n%2%n%C, %S%n%I",
        new String[] { "Nevis","St. Kitts" }, null, null), //
    LC("Saint Lucia", false, false, null, null, null, null, null), //
    MF("Saint Martin (french Part)", false, false, "9[78][01]\\d{2}", "%O%n%N%n%1%n%2%n%Z %C %X%n%I", null, null, null), //
    PM("Saint Pierre And Miquelon", false, false, "9[78]5\\d{2}", "%O%n%N%n%1%n%2%n%Z %C %X%n%I", null, null, null), //
    VC("Saint Vincent And The Grenadines", false, false, "VC\\d{4}", null, null, null, null), //
    WS("Samoa", false, false, null, null, null, null, null), //
    SM("San Marino", false, false, "4789\\d", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    ST("Sao Tome And Principe", false, false, null, "%N%n%O%n%1%n%2%n%C %X%n%I", null, null, null), //
    SA("Saudi Arabia", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%C %Z%n%I", null, null, null), //
    SN("Senegal", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    RS("Serbia", false, false, "\\d{5,6}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    SC("Seychelles", false, false, null, "%N%n%O%n%1%n%2%n%C%n%S%n%I", null, null, null), //
    SL("Sierra Leone", false, false, null, null, null, null, null), //
    SG("Singapore", false, false, "\\d{6}", "%N%n%O%n%1%n%2%nSINGAPORE %Z%n%I", null, null, null), //
    SX("Sint Maarten (dutch Part)", false, false, null, null, null, null, null), //
    SK("Slovakia", true, true, "\\d{3}[ ]?\\d{2}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    SI("Slovenia", true, true, "\\d{4}", "%N%n%O%n%1%n%2%nSI- %Z %C%n%I", null, null, null), //
    SB("Solomon Islands", false, false, null, null, null, null, null), //
    SO("Somalia", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%C, %S %Z%n%I",
        new String[] { "AD","BK","BN","BR","BY","GG","GD","HR","JD","JH","MD","NG","SG","SD","SH","SL","TG","WG" },
        new String[] { "AWDAL","BAKOOL","BANAADIR","BARI","BAY","GALGUDUUD","GEDO","HIIRAAN","JUBBADA DHEXE","JUBBADA HOOSE","MUDUG","NUGAAL","SANAAG","SHABEELLAHA DHEXE","SHABEELLAHA HOOSE","SOOL","TOGDHEER","WOQOOYI GALBEED" }, null), //
    ZA("South Africa", false, false, "\\d{4}", "%N%n%O%n%1%n%2%n%D%n%C%n%Z%n%I", null, null, null), //
    GS("South Georgia And The South Sandwich Islands", false, false, "SIQQ 1ZZ", "%N%n%O%n%1%n%2%n%X%n%C%n%Z%n%I", null, null, null), //
    SS("South Sudan", false, false, null, null, null, null, null), //
    ES("Spain", true, true, "\\d{5}", "%N%n%O%n%1%n%2%n%Z %C %S%n%I",
        new String[] { "A Coruña","Álava","Albacete","Alicante","Almería","Asturias","Ávila","Badajoz","Barcelona","Burgos","Cáceres","Cádiz","Cantabria","Castellón","Ceuta","Ciudad Real","Córdoba","Cuenca","Girona","Granada","Guadalajara","Guipúzcoa","Huelva","Huesca","Islas Baleares","Jaén","La Rioja","Las Palmas","León","Lleida","Lugo","Madrid","Málaga","Melilla","Murcia","Navarra","Ourense","Palencia","Pontevedra","Salamanca","Santa Cruz de Tenerife","Segovia","Sevilla","Soria","Tarragona","Teruel","Toledo","Valencia","Valladolid","Vizcaya","Zamora","Zaragoza" }, null, null), //
    LK("Sri Lanka", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%C%n%Z%n%I", null, null, null), //
    SD("Sudan", false, false, null, null, null, null, null), //
    SR("Suriname", false, false, null, "%N%n%O%n%1%n%2%n%C %X%n%S%n%I",
        new String[] { "BROKOPONDO","COMMEWIJNE","CORONIE","MAROWIJNE","NICKERIE","PARA","PARAMARIBO","SARAMACCA","SIPALIWINI","WANICA" }, null, null), //
    SJ("Svalbard And Jan Mayen", false, false, "\\d{4}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    SZ("Swaziland", false, false, "[HLMS]\\d{3}", "%N%n%O%n%1%n%2%n%C%n%Z%n%I", null, null, null), //
    SE("Sweden", true, false, "\\d{3}[ ]?\\d{2}", "%O%n%N%n%1%n%2%nSE-%Z %C%n%I", null, null, null), //
    CH("Switzerland", false, false, "\\d{4}", "%O%n%N%n%1%n%2%nCH-%Z %C%n%I",
        new String[] { "AG","AR","AI","BL","BS","BE","FR","GE","GL","GR","JU","LU","NE","NW","OW","SH","SZ","SO","SG","TI","TG","UR","VD","VS","ZG","ZH" },
        new String[] { "Aargau","Appenzell Ausserrhoden","Appenzell Innerrhoden","Basel-Landschaft","Basel-Stadt","Bern","Freiburg","Genf","Glarus","Graubünden","Jura","Luzern","Neuenburg","Nidwalden","Obwalden","Schaffhausen","Schwyz","Solothurn","St. Gallen","Tessin","Thurgau","Uri","Waadt","Wallis","Zug","Zürich" }, null), //
    SY("Syrian Arab Republic", false, false, null, null, null, null, null), //
    TW("Taiwan, Province Of China", false, false, "\\d{3}(\\d{2})?", "%Z%n%S%C%n%1%n%2%n%O%n%N%n%I",
        new String[] { "台中市","台北市","台東縣","台南市","宜蘭縣","花蓮縣","金門縣","南投縣","屏東縣","苗栗縣","桃園縣","高雄市","基隆市","連江縣","雲林縣","新北市","新竹市","新竹縣","嘉義市","嘉義縣","彰化縣","澎湖縣" }, null,
        new String[] { "Taichung City","Taipei City","Taitung County","Tainan City","Yilan County","Hualien County","Kinmen County","Nantou County","Pingtung County","Miaoli County","Taoyuan County","Kaohsiung City","Keelung City","Lienchiang County","Yunlin County","New Taipei City","Hsinchu City","Hsinchu County","Chiayi City","Chiayi County","Changhua County","Penghu County" }), //
    TJ("Tajikistan", false, false, "\\d{6}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    TZ("Tanzania, United Republic Of", false, false, "\\d{4}", null, null, null, null), //
    TH("Thailand", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%D %C%n%S %Z%n%I",
        new String[] { "กระบี่","กรุงเทพมหานคร","กาญจนบุรี","กาฬสินธุ์","กำแพงเพชร","ขอนแก่น","จังหวัด บึงกาฬ","จันทบุรี","ฉะเชิงเทรา","ชลบุรี","ชัยนาท","ชัยภูมิ","ชุมพร","เชียงราย","เชียงใหม่","ตรัง","ตราด","ตาก","นครนายก","นครปฐม","นครพนม","นครราชสีมา","นครศรีธรรมราช","นครสวรรค์","นนทบุรี","นราธิวาส","น่าน","บุรีรัมย์","ปทุมธานี","ประจวบคีรีขันธ์","ปราจีนบุรี","ปัตตานี","พระนครศรีอยุธยา","พะเยา","พังงา","พัทลุง","พิจิตร","พิษณุโลก","เพชรบุรี","เพชรบูรณ์","แพร่","ภูเก็ต","มหาสารคาม","มุกดาหาร","แม่ฮ่องสอน","ยโสธร","ยะลา","ร้อยเอ็ด","ระนอง","ระยอง","ราชบุรี","ลพบุรี","ลำปาง","ลำพูน","เลย","ศรีสะเกษ","สกลนคร","สงขลา","สตูล","สมุทรปราการ","สมุทรสงคราม","สมุทรสาคร","สระแก้ว","สระบุรี","สิงห์บุรี","สุโขทัย","สุพรรณบุรี","สุราษฎร์ธานี","สุรินทร์","หนองคาย","หนองบัวลำภู","อ่างทอง","อำนาจเจริญ","อุดรธานี","อุตรดิตถ์","อุทัยธานี","อุบลราชธานี" }, null,
        new String[] { "Krabi","Bangkok","Kanchanaburi","Kalasin","Kamphaeng Phet","Khon Kaen","Bueng Kan","Chanthaburi","Chachoengsao","Chon Buri","Chai Nat","Chaiyaphum","Chumpon","Chiang Rai","Chiang Mai","Trang","Trat","Tak","Nakhon Nayok","Nakhon Pathom","Nakhon Phanom","Nakhon Ratchasima","Nakhon Si Thammarat","Nakhon Sawan","Nonthaburi","Narathiwat","Nan","Buri Ram","Pathum Thani","Prachuap Khiri Khan","Prachin Buri","Pattani","Phra Nakhon Si Ayutthaya","Phayao","Phang Nga","Phattalung","Phichit","Phitsanulok","Phetchaburi","Phetchabun","Phrae","Phuket","Maha Sarakham","Mukdahan","Mae Hong Son","Yasothon","Yala","Roi Et","Ranong","Rayong","Ratchaburi","Lop Buri","Lampang","Lamphun","Loei","Si Sa Ket","Sakon Nakhon","Songkhla","Satun","Samut Prakan","Samut Songkhram","Samut Sakhon","Sa Kaeo","Saraburi","Sing Buri","Sukhothai","Suphanburi","Surat Thani","Surin","Nong Khai","Nong Bua Lam Phu","Ang Thong","Amnat Charoen","Udon Thani","Uttaradit","Uthai Thani","Ubon Ratchathani" }), //
    TL("Timor-leste", false, false, null, null, null, null, null), //
    TG("Togo", false, false, null, null, null, null, null), //
    TK("Tokelau", false, false, null, null, null, null, null), //
    TO("Tonga", false, false, null, null, null, null, null), //
    TT("Trinidad And Tobago", false, false, null, null, null, null, null), //
    TN("Tunisia", false, false, "\\d{4}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    TR("Turkey", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%Z %C/%S%n%I",
        new String[] { "Adana","Adıyaman","Afyon","Ağrı","Aksaray","Amasya","Ankara","Antalya","Ardahan","Artvin","Aydın","Balıkesir","Bartın","Batman","Bayburt","Bilecik","Bingöl","Bitlis","Bolu","Burdur","Bursa","Çanakkale","Çankırı","Çorum","Denizli","Diyarbakır","Düzce","Edirne","Elazığ","Erzincan","Erzurum","Eskişehir","Gaziantep","Giresun","Gümüşhane","Hakkari","Hatay","Iğdır","Isparta","İstanbul","İzmir","Kahramanmaraş","Karabük","Karaman","Kars","Kastamonu","Kayseri","Kırıkkale","Kırklareli","Kırşehir","Kilis","Kocaeli","Konya","Kütahya","Malatya","Manisa","Mardin","Mersin","Muğla","Muş","Nevşehir","Niğde","Ordu","Osmaniye","Rize","Sakarya","Samsun","Siirt","Sinop","Sivas","Şanlıurfa","Şırnak","Tekirdağ","Tokat","Trabzon","Tunceli","Uşak","Van","Yalova","Yozgat","Zonguldak" }, null, null), //
    TM("Turkmenistan", false, false, "\\d{6}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    TC("Turks And Caicos Islands", false, false, "TKCA 1ZZ", "%N%n%O%n%1%n%2%n%X%n%C%n%Z%n%I", null, null, null), //
    TV("Tuvalu", false, false, null, "%N%n%O%n%1%n%2%n%X%n%C%n%S%n%I",
        new String[] { "FUNAFUTI","NANUMANGA","NANUMEA","NIULAKITA","NIUTAO","NUI","NUKUFETAU","NUKULAELAE","VAITUPU" }, null, null), //
    UG("Uganda", false, false, null, null, null, null, null), //
    UA("Ukraine", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%C%n%S%n%Z%n%I",
        new String[] { "Автономна Республіка Крим","Вінницька область","Волинська область","Дніпропетровська область","Донецька область","Житомирська область","Закарпатська область","Запорізька область","Івано-Франківська область","місто Київ","Київська область","Кіровоградська область","Луганська область","Львівська область","Миколаївська область","Одеська область","Полтавська область","Рівненська область","місто Севастополь","Сумська область","Тернопільська область","Харківська область","Херсонська область","Хмельницька область","Черкаська область","Чернівецька область","Чернігівська область" },
        new String[] { "Автономна Республіка Крим","Вінницька область","Волинська область","Дніпропетровська область","Донецька область","Житомирська область","Закарпатська область","Запорізька область","Івано-Франківська область","Київ","Київська область","Кіровоградська область","Луганська область","Львівська область","Миколаївська область","Одеська область","Полтавська область","Рівненська область","Севастополь","Сумська область","Тернопільська область","Харківська область","Херсонська область","Хмельницька область","Черкаська область","Чернівецька область","Чернігівська область" },
        new String[] { "Crimea","Vinnyts'ka oblast","Volyns'ka oblast","Dnipropetrovsk Oblast","Donetsk Oblast","Zhytomyrs'ka oblast","Zakarpats'ka oblast","Zaporiz'ka oblast","Ivano-Frankivs'ka oblast","Kyiv city","Kiev Oblast","Kirovohrads'ka oblast","Luhans'ka oblast","Lviv Oblast","Mykolaivs'ka oblast","Odessa Oblast","Poltavs'ka oblast","Rivnens'ka oblast","Sevastopol' city","Sums'ka oblast","Ternopil's'ka oblast","Kharkiv Oblast","Khersons'ka oblast","Khmel'nyts'ka oblast","Cherkas'ka oblast","Chernivets'ka oblast","Chernihivs'ka oblast" }), //
    AE("United Arab Emirates", false, false, null, "%N%n%O%n%1%n%2%n%S%n%I",
        new String[] { "أبو ظبي","الفجيرة","ام القيوين","إمارة الشارقةّ","إمارة دبيّ","إمارة رأس الخيمة","عجمان" }, null,
        new String[] { "Abu Dhabi","Fujairah","Umm Al Quwain","Sharjah","Dubai","Ras al Khaimah","Ajmān" }), //
    GB("United Kingdom", true, false, "GIR[ ]?0AA|((AB|AL|B|BA|BB|BD|BH|BL|BN|BR|BS|BT|BX|CA|CB|CF|CH|CM|CO|CR|CT|CV|CW|DA|DD|DE|DG|DH|DL|DN|DT|DY|E|EC|EH|EN|EX|FK|FY|G|GL|GY|GU|HA|HD|HG|HP|HR|HS|HU|HX|IG|IM|IP|IV|JE|KA|KT|KW|KY|L|LA|LD|LE|LL|LN|LS|LU|M|ME|MK|ML|N|NE|NG|NN|NP|NR|NW|OL|OX|PA|PE|PH|PL|PO|PR|RG|RH|RM|S|SA|SE|SG|SK|SL|SM|SN|SO|SP|SR|SS|ST|SW|SY|TA|TD|TF|TN|TQ|TR|TS|TW|UB|W|WA|WC|WD|WF|WN|WR|WS|WV|YO|ZE)(\\d[\\dA-Z]?[ ]?\\d[ABD-HJLN-UW-Z]{2}))|BFPO[ ]?\\d{1,4}", "%N%n%O%n%1%n%2%n%C%n%S%n%Z%n%I", null, null, null), //
    US("United States", false, false, "(\\d{5})(?:[ \\-](\\d{4}))?", "%N%n%O%n%1%n%2%n%C, %S %Z%n%I",
        new String[] { "AL","AK","AS","AZ","AR","AA","AE","AP","CA","CO","CT","DE","DC","FL","GA","GU","HI","ID","IL","IN","IA","KS","KY","LA","ME","MH","MD","MA","MI","FM","MN","MS","MO","MT","NE","NV","NH","NJ","NM","NY","NC","ND","MP","OH","OK","OR","PW","PA","PR","RI","SC","SD","TN","TX","UT","VT","VI","VA","WA","WV","WI","WY" },
        new String[] { "Alabama","Alaska","American Samoa","Arizona","Arkansas","Armed Forces (AA)","Armed Forces (AE)","Armed Forces (AP)","California","Colorado","Connecticut","Delaware","District of Columbia","Florida","Georgia","Guam","Hawaii","Idaho","Illinois","Indiana","Iowa","Kansas","Kentucky","Louisiana","Maine","Marshall Islands","Maryland","Massachusetts","Michigan","Micronesia","Minnesota","Mississippi","Missouri","Montana","Nebraska","Nevada","New Hampshire","New Jersey","New Mexico","New York","North Carolina","North Dakota","Northern Mariana Islands","Ohio","Oklahoma","Oregon","Palau","Pennsylvania","Puerto Rico","Rhode Island","South Carolina","South Dakota","Tennessee","Texas","Utah","Vermont","Virgin Islands","Virginia","Washington","West Virginia","Wisconsin","Wyoming" }, null), //
    UM("United States Minor Outlying Islands", false, false, "96898", "%N%n%O%n%1%n%2%n%C %S %Z%n%I", null, null, null), //
    UY("Uruguay", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%Z %C %S%n%I",
        new String[] { "ARTIGAS","CANELONES","CERRO LARGO","COLONIA","DURAZNO","FLORES","FLORIDA","LAVALLEJA","MALDONADO","MONTEVIDEO","PAYSANDÚ","RÍO NEGRO","RIVERA","ROCHA","SALTO","SAN JOSÉ","SORIANO","TACUAREMBÓ","TREINTA Y TRES" }, null, null), //
    UZ("Uzbekistan", false, false, "\\d{6}", "%N%n%O%n%1%n%2%n%Z %C%n%S%n%I", null, null, null), //
    VU("Vanuatu", false, false, null, null, null, null, null), //
    VE("Venezuela, Bolivarian Republic Of", false, false, "\\d{4}", "%N%n%O%n%1%n%2%n%C %Z, %S%n%I",
        new String[] { "Amazonas","Anzoátegui","Apure","Aragua","Barinas","Bolívar","Carabobo","Cojedes","Delta Amacuro","Dependencias Federales","Distrito Federal","Falcón","Guárico","Lara","Mérida","Miranda","Monagas","Nueva Esparta","Portuguesa","Sucre","Táchira","Trujillo","Vargas","Yaracuy","Zulia" }, null, null), //
    VN("Viet Nam", false, false, "\\d{6}", "%N%n%O%n%1%n%2%n%C%n%S %Z%n%I",
        new String[] { "An Giang","Bà Rịa–Vũng Tàu","Bắc Giang","Bắc Kạn","Bắc Lieu","Bắc Ninh","Bến Tre","Bình Dương","Bình Định","Bình Phước","Bình Thuận","Cà Mau","Cao Bằng","Cần Thơ","Đà Nẵng","Đắk Lắk","Đăk Nông","Điện Biên","Đồng Nai","Đồng Tháp","Gia Lai","Hà Giang","Hà Nam","Hà Nội","Hà Tây","Hà Tĩnh","Hải Dương","Hải Phòng","Hậu Giang","Hòa Bình","Hưng Yên","Khánh Hòa","Kiên Giang","Kon Tum","Lai Châu","Lạng Sơn","Lào Cai","Lâm Đồng","Long An","Nam Định","Nghệ An","Ninh Bình","Ninh Thuận","Phú Thọ","Phú Yên","Quảng Bình","Quảng Nam","Quảng Ngãi","Quảng Ninh","Quảng Trị","Sóc Trăng","Sơn La","Tây Ninh","Thái Bình","Thái Nguyên","Thanh Hóa","Thành phố Hồ Chí Minh","Thừa Thiên–Huế","Tiền Giang","Trà Vinh","Tuyên Quang","Vĩnh Long","Vĩnh Phúc","Yên Bái" }, null,
        new String[] { "AN GIANG PROVINCE","BA RIA-VUNG TAU PROVINCE","BAC GIANG PROVINCE","BAC KAN PROVINCE","BAC LIEU PROVINCE","BAC NINH PROVINCE","BEN TRE PROVINCE","BINH DUONG PROVINCE","BINH DINH PROVINCE","BINH PHUOC PROVINCE","BINH THUAN PROVINCE","CA MAU PROVINCE","CAO BANG PROVINCE","CAN THO CITY","DA NANG CITY","DAK LAK PROVINCE","DAK NONG PROVINCE","DIEN BIEN PROVINCE","DONG NAI PROVINCE","DONG THAP PROVINCE","GIA LAI PROVINCE","HA GIANG PROVINCE","HA NAM PROVINCE","HANOI CITY","HA TAY PROVINCE","HA TINH PROVINCE","HAI DUONG PROVINCE","HAIPHONG CITY","HAU GIANG PROVINCE","HOA BINH PROVINCE","HUNG YEN PROVINCE","KHANH HOA PROVINCE","KIEN GIANG PROVINCE","KON TUM PROVINCE","LAI CHAU PROVINCE","LANG SONG PROVINCE","LAO CAI PROVINCE","LAM DONG PROVINCE","LONG AN PROVINCE","NAM DINH PROVINCE","NGHE AN PROVINCE","NINH BINH PROVINCE","NINH THUAN PROVINCE","PHU THO PROVINCE","PHU YEN PROVINCE","QUANG BINH PROVINCE","QUANG NAM PROVINCE","QUANG NGAI PROVINCE","QUANG NINH PROVINCE","QUANG TRI PROVINCE","SOC TRANG PROVINCE","SON LA PROVINCE","TAY NINH PROVINCE","THAI BINH PROVINCE","THAI NGUYEN PROVINCE","THANH HOA PROVINCE","HO CHI MINH CITY","THUA THIEN-HUE PROVINCE","TIEN GIANG PROVINCE","TRA VINH PROVINCE","TUYEN QUANG PROVINCE","VINH LONG PROVINCE","VINH PHUC PROVINCE","YEN BAI PROVINCE" }), //
    VG("Virgin Islands, British", false, false, "VG\\d{4}", "%N%n%O%n%1%n%2%n%C%n%Z%n%I", null, null, null), //
    VI("Virgin Islands, U.s.", false, false, "(008(?:(?:[0-4]\\d)|(?:5[01])))(?:[ \\-](\\d{4}))?", "%N%n%O%n%1%n%2%n%C %S %Z%n%I", null, null, null), //
    WF("Wallis And Futuna", false, false, "986\\d{2}", "%O%n%N%n%1%n%2%n%Z %C %X%n%I", null, null, null), //
    EH("Western Sahara", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    YE("Yemen", false, false, null, null, null, null, null), //
    ZM("Zambia", false, false, "\\d{5}", "%N%n%O%n%1%n%2%n%Z %C%n%I", null, null, null), //
    ZW("Zimbabwe", false, false, null, null, null, null, null); //
    // @formatter:on

    private static final long serialVersionUID = 1L;

    private static transient final Map<String, IsoCountryCode> CODES;

    static {
        final Map<String, IsoCountryCode> countryNames = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (final IsoCountryCode country : IsoCountryCode.values()) {
            countryNames.put(country.name(), country);
        }
        CODES = Collections.unmodifiableMap(countryNames);
    }

    public static IsoCountryCode findCountry(final String postalCode, final String stateOrProvince,
            final IsoCountryCode... countries) {
        final int numCountries = countries != null ? countries.length : 0;
        if (numCountries == 0) {
            return findCountry(postalCode, stateOrProvince, IsoCountryCode.values());
        }
        final boolean havePostalCode = !StringUtils.isBlank(postalCode);
        final boolean haveStateOrProvince = !StringUtils.isBlank(stateOrProvince);
        if (!havePostalCode && !haveStateOrProvince) {
            // no criteria means no match
            return null;
        }
        for (int i = 0; i < numCountries; i++) {
            final IsoCountryCode country = countries[i];
            if (havePostalCode && !country.isValidPostalCode(postalCode)) {
                continue;
            }
            if (haveStateOrProvince && !country.isValidStateOrProvince(stateOrProvince)) {
                continue;
            }
            return country;
        }
        return null;
    }

    /**
     * Returns {@link IsoCountryCode} for given locale.
     *
     * @param locale
     *            locale
     * @return {@link IsoCountryCode} instance
     */
    public static IsoCountryCode fromLocaleCountry(final Locale locale) {
        if (locale != null) {
            return fromString(locale.getCountry());
        }
        return null;
    }

    /**
     * Returns {@link IsoCountryCode} for given ISO 3166-1 identifier (case-insensitive).
     *
     * @param countryCode
     *            ISO 3166-1 country code
     * @return {@link IsoCountryCode} instance
     */
    public static IsoCountryCode fromString(final String countryCode) {
        if (!StringUtils.isEmpty(countryCode)) {
            return CODES.get(countryCode);
        }
        return null;
    }

    /** English country name **/
    private final String countryName;

    /** True if country is member of EU **/
    private final boolean eu;

    /** True if country uses Euro **/
    private final boolean euro;

    /**
     * Regular expression for matching postal code; might still be invalid postal code this just a
     * sanity check measure
     **/
    private final Pattern postalCodePattern;

    /**
     * Address format.
     *
     * The format string is a textual template containing literal text, formatting characters and
     * placeholders for the address fields (as identified by their one-letter abbreviations). Formatting
     * characters and address field placeholders are prefixed by a ‘%’ character, while all other text
     * is literal. Formatting characters (such as “%n”) and literal text are ignored for validation
     * purposes.
     *
     * For example, the US data has a "fmt" value of "%N%n%O%n%A%n%C %S %Z". This means that the allowed
     * fields are N, O, A, C, S and Z, which according to the definitions above correspond to name,
     * organisation, street address lines, city, administrative area and postal code respectively.
     * <ul>
     * <li>n – newline</li>
     * <li>N – Name</li>
     * <li>O – Organisation</li>
     * <li>1 – Address line 1</li>
     * <li>2 – Address line 2</li>
     * <li>D – Dependent locality (may be an inner-city district or a suburb)</li>
     * <li>C – City or Locality</li>
     * <li>S – Administrative area such as a state, province, island etc</li>
     * <li>Z – Zip or postal code</li>
     * <li>X – Sorting code</li>
     * <li>I – Country</li>
     * </ul>
     **/
    private final String addressFormat;

    /** List of state/province codes **/
    private final ImmutableSortedSet<String> statesProvinces;

    /** List of state/province names **/
    private final ImmutableSortedSet<String> stateProvinceNames;

    /** List of state/province names (stripped of diacritics) **/
    private final ImmutableSortedSet<String> stateProvinceLatinNames;

    private IsoCountryCode(final String countryName, final boolean eu, final boolean euro,
            final String postalCodePattern, final String addressFormat, final String[] states,
            final String[] stateNames, final String[] stateLatinNames) {
        this.countryName = countryName;
        this.eu = eu;
        this.euro = euro;
        this.postalCodePattern = StringUtils.isEmpty(postalCodePattern) ? null
                : Pattern.compile("^\\s*" + postalCodePattern + "\\s*$",
                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        this.addressFormat = addressFormat;
        this.statesProvinces = states != null
                ? ImmutableSortedSet.orderedBy(String.CASE_INSENSITIVE_ORDER).add(states).build()
                : ImmutableSortedSet.of();
        this.stateProvinceNames = stateNames != null
                ? ImmutableSortedSet.orderedBy(String.CASE_INSENSITIVE_ORDER).add(stateNames).build()
                : ImmutableSortedSet.of();
        this.stateProvinceLatinNames = stateLatinNames != null
                ? ImmutableSortedSet.orderedBy(String.CASE_INSENSITIVE_ORDER).add(stateLatinNames).build()
                : ImmutableSortedSet.of();
    }

    public String getAddressFormat() {
        // default to US format if not specified
        return addressFormat != null && addressFormat.length() != 0 ? addressFormat : US.addressFormat;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getResourceBundleKey() {
        return getClass().getName() + "." + name();
    }

    public Set<String> getStateProvinceLatinNames() {
        return stateProvinceLatinNames;
    }

    public Set<String> getStateProvinceNames() {
        return stateProvinceNames;
    }

    public Set<String> getStatesProvinces() {
        return statesProvinces;
    }

    public boolean isEu() {
        return eu;
    }

    public boolean isEuro() {
        return euro;
    }

    public boolean isValidPostalCode(final String postalCode) {
        if (postalCodePattern == null || StringUtils.isEmpty(postalCode)) {
            return false;
        }
        return postalCodePattern.matcher(postalCode).find();
    }

    public boolean isValidStateOrProvince(final String stateProvince) {
        if (StringUtils.isEmpty(stateProvince)) {
            return false;
        }
        return statesProvinces.contains(stateProvince) || stateProvinceNames.contains(stateProvince)
                || stateProvinceLatinNames.contains(stateProvince);
    }
}
