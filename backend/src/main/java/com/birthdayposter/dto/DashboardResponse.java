package com.birthdayposter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private long totalPeople;
    private long birthdaysToday;
    private long activeTemplates;
    private long postersGenerated;
    
    private List<UpcomingBirthdayDto> upcomingBirthdays;
    private List<RecentPosterDto> recentPosters;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpcomingBirthdayDto {
        private Long id;
        private String name;
        private String initials;
        private String department;
        private String dob;
        private String daysUntilText; // e.g., "Tomorrow", "In 3 days"
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentPosterDto {
        private Long id;
        private String personName;
        private String timeAgoText;
        private String posterUrl;
    }
}
