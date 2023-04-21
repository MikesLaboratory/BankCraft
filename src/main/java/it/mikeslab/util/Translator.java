/*
 * GNU GENERAL PUBLIC LICENSE
 * Version 3, 29 June 2007
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package it.mikeslab.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The type Translator.
 */
public class Translator {

    /**
     * Translate collection.
     *
     * @param stringList the string list
     * @param replaceMap the replace map
     * @return the collection
     */
    public static Collection<Component> translate(List<String> stringList, Map<String, String> replaceMap) {
        Collection<Component> componentList = new ArrayList<>();

        for (String string : stringList) {
            for(Map.Entry<String, String> entry : replaceMap.entrySet()) {
                string = string.replace(entry.getKey(), entry.getValue());
            }

            componentList.add(translate(string));
        }

        return componentList;
    }


    /**
     * Translate component.
     *
     * @param string the string
     * @return the component
     */
    public static Component translate(String string) {
        return MiniMessage.miniMessage().deserialize(string).decoration(TextDecoration.ITALIC, false);
    }

    /**
     * Translate string.
     *
     * @param component the component
     * @return the string
     */
    public static String translate(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }


    /**
     * Legacy translate string.
     *
     * @param string the string
     * @return the string
     */
    public static String legacyTranslate(String string) {
        return translate(translate(string));
    }

    /**
     * Legacy translate string.
     *
     * @param string      the string
     * @param replaceable the replaceable
     * @return the string
     */
    public static String legacyTranslate(String string, Map<String, String> replaceable) {
        Component component = translate(string);

        for(String key : replaceable.keySet()) {
            component = component.replaceText(TextReplacementConfig.builder().matchLiteral(key).replacement(replaceable.get(key)).build());
        }

        return translate(component);
    }


    /**
     * Legacy list translate list.
     *
     * @param stringList the string list
     * @return the list
     */
    public static List<String> legacyListTranslate(List<String> stringList) {
        List<String> translatedList = new ArrayList<>();
        for (String string : stringList) {
            translatedList.add(legacyTranslate(string));
        }
        return translatedList;
    }



}
