package sample.helpers;


import java.util.prefs.Preferences;

public class UserPrefs {

    private static final String COMMANDS = "COMMANDS";


    public static Preferences getPrefs()
    {
        // Retrieve the user preference node for the package com.mycompany
        Preferences prefs = Preferences.userNodeForPackage(UserPrefs.class);
        return prefs;
    }


    public static String getValue(String prefName, String def)
    {
        return getPrefs().get(prefName, def);
    }

    public static void setValue(String prefName, String value)
    {
        getPrefs().put(prefName, value);
    }

    public static void setCommands(String commands)
    {
        setValue(COMMANDS, commands.trim());
    }

    public static String getCommands()
    {

        return getValue(COMMANDS, "").trim();

    }


}
