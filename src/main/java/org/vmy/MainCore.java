package org.vmy;

import org.vmy.FileWatcher;
import org.vmy.GraphBot;

public class MainCore {
    public static void main(String[] args) throws Exception {
        if (args.length<1)
            throw new Exception("You must provide mainClass to run as a parameter.");
        String mainName = args[0];
        switch (mainName) {
            case "FileWatcher": FileWatcher.main(args); break;
            case "ParseBot": ParseBot.main(args); break;
            case "GraphBot": GraphBot.main(args); break;
            //case "DiscordBot": org.vmy.DiscordBot.main(args); break;
            default: throw new Exception("Unknown mainClass: " + mainName);
        }
    }
}
