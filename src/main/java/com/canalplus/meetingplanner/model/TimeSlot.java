package com.canalplus.meetingplanner.model;

import java.util.Optional;

/**
 * Représente un créneau horaire
 * Les valeurs vont de EIGHT_NINE (8h-9h) à NINETEEN_TWENTY (19h-20h)
 */
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
