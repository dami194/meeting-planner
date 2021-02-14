package com.canalplus.meetingplanner.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static com.canalplus.meetingplanner.model.TimeSlot.*;
import static org.assertj.core.api.Assertions.assertThat;

class TimeSlotTest {

    @ParameterizedTest
    @MethodSource("provideNextSlots")
    void check_nextSlots(TimeSlot actual, Optional<TimeSlot> next) {
        assertThat(actual.nextSlot()).isEqualTo(next);
    }

    @ParameterizedTest
    @MethodSource("providePreviousSlots")
    void check_previousSlots(TimeSlot actual, Optional<TimeSlot> previous) {
        assertThat(actual.previousSlot()).isEqualTo(previous);
    }

    private static Stream<Arguments> provideNextSlots() {
        return Stream.of(
                Arguments.of(EIGHT_NINE, Optional.of(NINE_TEN)),
                Arguments.of(NINE_TEN, Optional.of(TEN_ELEVEN)),
                Arguments.of(TEN_ELEVEN, Optional.of(ELEVEN_TWELVE)),
                Arguments.of(ELEVEN_TWELVE, Optional.of(TWELVE_THIRTEEN)),
                Arguments.of(TWELVE_THIRTEEN, Optional.of(THIRTEEN_FOURTEEN)),
                Arguments.of(THIRTEEN_FOURTEEN, Optional.of(FOURTEEN_FIFTEEN)),
                Arguments.of(FOURTEEN_FIFTEEN, Optional.of(FIFTEEN_SIXTEEN)),
                Arguments.of(FIFTEEN_SIXTEEN, Optional.of(SIXTEEN_SEVENTEEN)),
                Arguments.of(SIXTEEN_SEVENTEEN, Optional.of(SEVENTEEN_EIGHTEEN)),
                Arguments.of(SEVENTEEN_EIGHTEEN, Optional.of(EIGHTEEN_NINETEEN)),
                Arguments.of(EIGHTEEN_NINETEEN, Optional.of(NINETEEN_TWENTY)),
                Arguments.of(NINETEEN_TWENTY, Optional.empty())
        );
    }

    private static Stream<Arguments> providePreviousSlots() {
        return Stream.of(
                Arguments.of(EIGHT_NINE, Optional.empty()),
                Arguments.of(NINE_TEN, Optional.of(EIGHT_NINE)),
                Arguments.of(TEN_ELEVEN, Optional.of(NINE_TEN)),
                Arguments.of(ELEVEN_TWELVE, Optional.of(TEN_ELEVEN)),
                Arguments.of(TWELVE_THIRTEEN, Optional.of(ELEVEN_TWELVE)),
                Arguments.of(THIRTEEN_FOURTEEN, Optional.of(TWELVE_THIRTEEN)),
                Arguments.of(FOURTEEN_FIFTEEN, Optional.of(THIRTEEN_FOURTEEN)),
                Arguments.of(FIFTEEN_SIXTEEN, Optional.of(FOURTEEN_FIFTEEN)),
                Arguments.of(SIXTEEN_SEVENTEEN, Optional.of(FIFTEEN_SIXTEEN)),
                Arguments.of(SEVENTEEN_EIGHTEEN, Optional.of(SIXTEEN_SEVENTEEN)),
                Arguments.of(EIGHTEEN_NINETEEN, Optional.of(SEVENTEEN_EIGHTEEN)),
                Arguments.of(NINETEEN_TWENTY, Optional.of(EIGHTEEN_NINETEEN))
        );
    }

}