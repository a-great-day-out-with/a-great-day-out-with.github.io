import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Convert {

    private static final Pattern SEPARATOR = Pattern.compile(";");

    private static String styles = """
                                   a, a:visited {
                                     text-decoration: underline;
                                     fill: #0077cc;
                                   }
                                   """;

    private static final String CONFIG_KEY_EMOJIS = "emojis.to.mirror";
    private static final String CONFIG_KEY_TWITTER = "twitter.handles";
    private static final String CONFIG_KEY_REPO = "journey.short.url";

    public static void main(String... args) throws Exception {
        if (args == null || args.length != 1) {
            System.out.println("Usage: java Convert.java <journey>");
            return;
        }
        String journey = args[0];
        System.out.println("Processing "+journey+" journey");
        Properties config = readConfig(journey);
        List<Entry> entries = getEntries(journey);
        Path output = createOutputFile(journey, entries, config);
        System.out.println("Created " + output);
    }

    private static Properties readConfig(String journey) throws Exception {
        if(!Files.exists(Path.of(journey+"/config.properties"))) {
            return new Properties();
        }
        try (InputStreamReader isr = new FileReader(new File(journey+"/config.properties"), StandardCharsets.UTF_8)) {
            Properties properties = new Properties();
            properties.load(isr);
            return properties;
        }
    }

    private static List<Entry> getEntries(String journey) throws Exception {
        List<Entry> entries = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(journey +  "/links.csv"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (isComment(line) || line.isEmpty()) {
                    continue;
                }
                entries.add(entryFromLine(line));
            }
        }

        return entries;
    }

    private static Path createOutputFile(String journey, List<Entry> entries, Properties config) throws Exception {
        Path source = Paths.get(journey + "/exported.svg");
        Path target = Paths.get("docs/" + journey + "/generated.svg");
        Charset charset = StandardCharsets.UTF_8;

        String content = new String(Files.readAllBytes(source), charset);

        for (Entry entry : entries) {
            content = content.replaceAll(Pattern.quote(">" + entry.title() + "</text>"), ">" + entry.toLink() + "</text>");
        }

        // inject styles
        content = content.replace("</style>", styles + "</style>");
        
        //mirror emojis where applicable according to configuration
        String emojiConfig = config.getProperty(CONFIG_KEY_EMOJIS);
        if(emojiConfig != null && !emojiConfig.isBlank()) {
            List<String> emojisToMirror = Arrays.stream(SEPARATOR.split(emojiConfig))
                                            .map(String::trim).collect(Collectors.toList());
            for(String emoji : emojisToMirror) {
                System.out.println("Searching occurrences of emoji "+emoji+ " to mirror");
                Matcher matcher = Pattern.compile(">" + emoji).matcher(content);
                if(matcher.find()) {
                    System.out.println("Found emoji: " + matcher.group());
                    content = matcher.replaceAll(" transform=\"scale(-1, 1)\">" + emoji);
                }
            }
        }
        
        // replace text with proper links where applicable according to configuration  
        // twitter handles
        String twitterHandleConfig = config.getProperty(CONFIG_KEY_TWITTER);
        if(twitterHandleConfig != null && !twitterHandleConfig.isBlank()) {
            List<String> twitterHandles = Arrays.stream(SEPARATOR.split(twitterHandleConfig))
                                            .map(String::trim).collect(Collectors.toList());
            for(String twitterHandle : twitterHandles) {
                System.out.println("Replace twitter handle "+twitterHandle+" with link to profile");
                content = content.replace(twitterHandle, "<a href=\"https://twitter.com/"+twitterHandle+"\" target=\"_top\">"+twitterHandle+"</a>");
            }
        }
        //repo URL
        String repoConfig = config.getProperty(CONFIG_KEY_REPO);
        if(repoConfig != null && !repoConfig.isBlank()) {
            System.out.println("Replace repo URL with link "+repoConfig);
            content = content.replace(repoConfig, "<a href=\""+repoConfig+"\" target=\"_top\">"+repoConfig+"</a>");
        }
        Files.write(target, content.getBytes(charset));

        return target;
    }

    private static boolean isComment(String line) {
        return line.startsWith("#");
    }

    private static Entry entryFromLine(String line) {
        String[] parts = SEPARATOR.split(line);

        if (parts.length != 2) {
            throw new IllegalArgumentException("Unexpected number of columns in line: " + line);
        }

        return new Entry(parts[0], parts[1]);
    }

    public static record Entry(String title, String url) {

        public String toLink() {
            return "<a href=\"" + url + "\" target=\"_top\">" + title + "</a>";
        }
    }
}
