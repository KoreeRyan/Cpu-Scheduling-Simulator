package ui;

import model.ScheduleResult;
import model.ScheduleSlice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

public class PlantUMLGanttExporter {

    public static void export(ScheduleResult result, Path outFile) throws IOException {
        LocalDate base = LocalDate.now();

        StringBuilder sb = new StringBuilder();
        sb.append("@startgantt\n");
        sb.append("Project starts ").append(base).append("\n");
        sb.append("printscale daily\n\n");

        for (ScheduleSlice s : result.getTimeline()) {
            if (s.getEnd() <= s.getStart()) continue;

            LocalDate startDate = base.plusDays(s.getStart());
            LocalDate endDate = base.plusDays(s.getEnd());

            sb.append("[")
                    .append(s.getPid())
                    .append("] starts ")
                    .append(startDate)
                    .append(" and ends ")
                    .append(endDate)
                    .append("\n");
        }

        sb.append("@endgantt\n");

        Files.createDirectories(outFile.getParent());
        Files.writeString(outFile, sb.toString());
    }
}

