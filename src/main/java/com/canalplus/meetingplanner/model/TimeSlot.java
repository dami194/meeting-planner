package com.canalplus.meetingplanner.model;

import java.util.Optional;

public enum TimeSlot {
    EIGHT_NINE,
    NINE_TEN,
    TEN_ELEVEN,
    ELEVEN_TWELVE,
    TWELVE_THIRTEEN,
    THIRTEEN_FOURTEEN,
    FOURTEEN_FIFTEEN,
    FIFTEEN_SIXTEEN,
    SIXTEEN_SEVENTEEN,
    SEVENTEEN_EIGHTEEN,
    EIGHTEEN_NINETEEN,
    NINETEEN_TWENTY;

    public Optional<TimeSlot> nextSlot() {
        switch(this) {
            case EIGHT_NINE: return Optional.of(NINE_TEN);
            case NINE_TEN: return Optional.of(TEN_ELEVEN);
            case TEN_ELEVEN: return Optional.of(ELEVEN_TWELVE);
            case ELEVEN_TWELVE: return Optional.of(TWELVE_THIRTEEN);
            case TWELVE_THIRTEEN: return Optional.of(THIRTEEN_FOURTEEN);
            case THIRTEEN_FOURTEEN: return Optional.of(FOURTEEN_FIFTEEN);
            case FOURTEEN_FIFTEEN: return Optional.of(FIFTEEN_SIXTEEN);
            case FIFTEEN_SIXTEEN: return Optional.of(SIXTEEN_SEVENTEEN);
            case SIXTEEN_SEVENTEEN: return Optional.of(SEVENTEEN_EIGHTEEN);
            case SEVENTEEN_EIGHTEEN: return Optional.of(EIGHTEEN_NINETEEN);
            case EIGHTEEN_NINETEEN: return Optional.of(NINETEEN_TWENTY);
            case NINETEEN_TWENTY:
            default: return Optional.empty();
        }
    }

    public Optional<TimeSlot> previousSlot() {
        switch(this) {
            case EIGHT_NINE: return Optional.empty();
            case NINE_TEN: return Optional.of(EIGHT_NINE);
            case TEN_ELEVEN: return Optional.of(NINE_TEN);
            case ELEVEN_TWELVE: return Optional.of(TEN_ELEVEN);
            case TWELVE_THIRTEEN: return Optional.of(ELEVEN_TWELVE);
            case THIRTEEN_FOURTEEN: return Optional.of(TWELVE_THIRTEEN);
            case FOURTEEN_FIFTEEN: return Optional.of(THIRTEEN_FOURTEEN);
            case FIFTEEN_SIXTEEN: return Optional.of(FOURTEEN_FIFTEEN);
            case SIXTEEN_SEVENTEEN: return Optional.of(FIFTEEN_SIXTEEN);
            case SEVENTEEN_EIGHTEEN: return Optional.of(SIXTEEN_SEVENTEEN);
            case EIGHTEEN_NINETEEN: return Optional.of(SEVENTEEN_EIGHTEEN);
            case NINETEEN_TWENTY:
            default: return Optional.of(EIGHTEEN_NINETEEN);
        }
    }

}
