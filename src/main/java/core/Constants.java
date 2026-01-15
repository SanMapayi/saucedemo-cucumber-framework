package core;

import java.nio.file.Path;

public class Constants {

    public static final Path SCREENSHOTSPATH = Path.of(System.getProperty("user.dir")).resolve(Path.of("screenshots"));
    public static final Path TESTREPORTPATH =  Path.of(System.getProperty("user.dir")).resolve(Path.of("test-output"));
    public static final String URL = "https://www.saucedemo.com";
    public static final int NUMBEROFTESTREPORTSTOKEEP = 5;
}
