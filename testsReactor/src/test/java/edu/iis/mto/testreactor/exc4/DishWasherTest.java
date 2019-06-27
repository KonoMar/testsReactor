package edu.iis.mto.testreactor.exc4;


import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class DishWasherTest {
    private DishWasher dishWasher;
    private DirtFilter dirtFilter;
    private Door door;
    private Engine engine;
    private RunResult runResult;
    private WaterPump waterPump;

    @Before
    public void setUp() {
        dirtFilter = mock(DirtFilter.class);
        door = mock(Door.class);
        engine = mock(Engine.class);
        waterPump = mock(WaterPump.class);
    }

    @Test
    public void shouldReturnTrueIfStatusIsErrorFilter() {
        //RunResult runResult = RunResult.builder().withRunMinutes(30).withStatus(Status.ERROR_FILTER).build();
        ProgramConfiguration programConfiguration = ProgramConfiguration.builder().withProgram(WashingProgram.ECO).withTabletsUsed(true).build();

        DishWasher dishWasher = new DishWasher(waterPump, engine, dirtFilter, door);
        dishWasher.start(programConfiguration);
        Assert.assertThat(dishWasher.start(programConfiguration).getStatus(), is(Status.ERROR_FILTER));
    }


    @Test
    public void shouldReturnTrueIfPourInvokesOnce() throws PumpException {
        ProgramConfiguration programConfiguration = ProgramConfiguration.builder().withProgram(WashingProgram.ECO).withTabletsUsed(true).build();

        DishWasher dishWasher = new DishWasher(waterPump, engine, dirtFilter, door);
        dishWasher.start(programConfiguration);

        WashingProgram washingProgram = new WashingProgram(120);
        verify(waterPump, times(1)).pour(washingProgram);
    }




}
