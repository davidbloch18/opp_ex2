package gym.management.Sessions;

import gym.ForumType;
import gym.SessionType;
import gym.management.Employee.Instructor.*;

class SessionNinja extends Session {
    public SessionNinja(SessionType sessionType, String date_and_time, Instructor instructor,
            ForumType sessionForum) {
        super(sessionType, date_and_time, instructor, sessionForum);
    }
}