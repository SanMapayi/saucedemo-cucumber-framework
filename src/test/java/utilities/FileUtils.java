package utilities;

import core.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class FileUtils {

    public static void deleteTestReports(int numberOfTestReportsToKeep) throws IOException {
        Path path = Constants.TESTREPORTPATH;


        try (var paths = Files.find(path, 1,
                (p, attr) -> attr.isRegularFile() && p.toString().contains("Test-Report"))) {
            var setOfTestReports = paths.collect(Collectors.toList());
            int countFile = setOfTestReports.size();


            if (countFile > 5) {
                final int countFileFinal = countFile - numberOfTestReportsToKeep;

                for (int i = 0; i < countFileFinal; i++) {
                    Path pathToDeleted = setOfTestReports.get(i);
                    System.out.println(pathToDeleted);
                    Files.deleteIfExists(pathToDeleted);

                    LoggerUtil.getLogger().info("{} has been deleted", pathToDeleted);
                }

            }
        }
        catch (IOException ioException) {
            LoggerUtil.getLogger().info(ioException.getMessage());
        }
    }

}
