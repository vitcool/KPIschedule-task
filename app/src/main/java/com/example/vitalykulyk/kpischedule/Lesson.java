package com.example.vitalykulyk.kpischedule;

/**
 * Created by Vitaly Kulyk on 23.02.2016.
 */
public class Lesson {

    public final String lesson_number   ;
    public final String lesson_room     ;
    public final String lesson_name     ;
    public final String lesson_type     ;
    public final String teacher_name    ;

        public Lesson (String number, String room,String name, String type, String teacher ) {
            this.lesson_number= number;
            this.lesson_room  = room;
            this.lesson_name  = name;
            this.lesson_type  = type;
            this.teacher_name = teacher;
        }

    public String getLesson_number() {
        return lesson_number;
    }

    public String getLesson_room() {
        return lesson_room;
    }

    public String getLesson_name() {
        return lesson_name;
    }

    public String getLesson_type() {
        return lesson_type;
    }

    public String getTeacher_name() {
        return teacher_name;
    }
}
