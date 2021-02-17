package com.canalplus.meetingplanner.exceptions;

public class NoAvailableRoomException extends IllegalStateException{

    public NoAvailableRoomException(String message) {
        super(message);
    }
}
