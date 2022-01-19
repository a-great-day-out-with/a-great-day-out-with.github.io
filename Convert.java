import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Convert {

    private static final Pattern SEPARATOR = Pattern.compile(";");

    private static String styles = """
                                   a, a:visited {
                                     text-decoration: underline;
                                     fill: #0077cc;
                                   }
                                   """;

    private static final List<String> emojisToMirror = Arrays.asList("🚂", "🚡", "🚴‍♂️", "⛵");

    public static void main(String... args) throws Exception {
        if (args == null || args.length != 1) {
            System.out.println("Usage: java Convert.java <journey>");
            return;
        }

        String journey = args[0];

        List<Entry> entries = getEntries(journey);
        Path output = createOutputFile(journey, entries);

        System.out.println("Created " + output);
    }

    private static List<Entry> getEntries(String journey) throws Exception {
        List<Entry> entries = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(journey +  "-links.csv"))) {
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

    private static Path createOutputFile(String journey, List<Entry> entries) throws Exception {
        Path source = Paths.get(journey + ".svg");
        Path target = Paths.get("docs/" + journey + ".svg");
        Charset charset = StandardCharsets.UTF_8;

        String content = new String(Files.readAllBytes(source), charset);

        for (Entry entry : entries) {
            content = content.replaceAll(Pattern.quote(">" + entry.title() + "</text>"), ">" + entry.toLink() + "</text>");
        }

        // inject styles
        content = content.replace("</style>", styles + "</style>");

        for(String emoji : emojisToMirror) {
            Matcher matcher = Pattern.compile(">" + emoji).matcher(content);
            System.out.println("Found: " + matcher.find() + " " + matcher.group());
            content = matcher.replaceAll(" transform=\"scale(-1, 1)\">" + emoji);
        }

        // twitter handles
        content = content.replace("@gunnarmorling", "<a href=\"https://twitter.com/gunnarmorling\" target=\"_top\">@gunnarmorling</a>");
        content = content.replace("@hpgrahsl", "<a href=\"https://twitter.com/hpgrahsl\" target=\"_top\">@hpgrahsl</a>");
        // github repo
        content = content.replace("https://bit.ly/journey4kafka", "<a href=\"https://bit.ly/journey4kafka\" target=\"_top\">https://bit.ly/journey4kafka</a>");
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
