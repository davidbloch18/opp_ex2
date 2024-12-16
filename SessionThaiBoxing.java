class SessionThiBoxing extends Session {

    SessionThiBoxing(SessionType sessionType, String date_and_time, Instructor admittedInstructor,
            ForumType sessionForum) {
        this.sessionType = sessionType;
        this.sessionForum = sessionForum;
        this.date_and_time = date_and_time;
        this.admittedInstructor = admittedInstructor;
    }

}