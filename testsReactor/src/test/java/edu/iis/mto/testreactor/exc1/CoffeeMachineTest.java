package edu.iis.mto.testreactor.exc1;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class CoffeeMachineTest {

    @Mock
    Grinder grinder;
    @Mock
    CoffeeReceipes receipes;
    @Mock
    MilkProvider milkProvider;

    private Map<CoffeeSize, Integer> coffeeSizes;
    private CoffeeMachine machine;

    @Before
    public void setUp() {
        coffeeSizes = new HashMap<>();
        coffeeSizes.put(CoffeeSize.SMALL, 1);
        coffeeSizes.put(CoffeeSize.STANDARD, 2);
        coffeeSizes.put(CoffeeSize.DOUBLE, 4);
        machine = new CoffeeMachine(grinder, milkProvider, receipes);
    }

    @Test
    public void itCompiles() {
        assertThat(true, equalTo(true));
    }

    @Test
    public void OrderForStandardCoffeeShouldReturnStandardCoffee() {
        CoffeOrder order = createOrderAndSetDefaultMockConfiguration(1, CoffeeSize.STANDARD, CoffeType.CAPUCCINO);
        Coffee result = machine.make(order);

        Assert.assertThat(result.getMilkAmout(), is(1));
        Assert.assertThat(result.getWaterAmount(), is(2));
    }

    @Test
    public void coffeeMachineShouldUseGrinderAndMilkProviderAndCoffeeReceipes() {
        machine.make(createOrderAndSetDefaultMockConfiguration(1, CoffeeSize.STANDARD, CoffeType.CAPUCCINO));

        verify(grinder, atLeastOnce()).grind(CoffeeSize.STANDARD);
        verify(receipes, atLeastOnce()).getReceipe(CoffeType.CAPUCCINO);
        verify(milkProvider, atLeastOnce()).pour(1);
    }

    @Test(expected = UnknownCofeeTypeException.class)
    public void orderWithoutReceipeShouldThrowException() {
        CoffeOrder order = createOrderAndSetDefaultMockConfiguration(1, CoffeeSize.STANDARD, CoffeType.CAPUCCINO);
        when(receipes.getReceipe(CoffeType.CAPUCCINO)).thenReturn(null);
        machine.make(order);
    }

    @Test(expected = NoCoffeeBeansException.class)
    public void coffeePreparationShouldFailWhenGrindingFails() {
        CoffeOrder order = createOrderAndSetDefaultMockConfiguration(1, CoffeeSize.STANDARD, CoffeType.CAPUCCINO);
        when(grinder.grind(CoffeeSize.STANDARD)).thenReturn(false);
        machine.make(order);
    }

    @Test
    public void coffeeShouldHaveCorrectAmountOfWaterBasedOnCoffeeSize() {
        CoffeOrder orderSmall = createOrderAndSetDefaultMockConfiguration(1, CoffeeSize.SMALL, CoffeType.CAPUCCINO);
        Coffee smallCoffee = machine.make(orderSmall);
        Assert.assertThat(smallCoffee.getWaterAmount(), is(1));

        CoffeOrder orderStandard = createOrderAndSetDefaultMockConfiguration(1, CoffeeSize.STANDARD, CoffeType.CAPUCCINO);
        Coffee standardCoffee = machine.make(orderStandard);
        Assert.assertThat(standardCoffee.getWaterAmount(), is(2));

        CoffeOrder orderDouble = createOrderAndSetDefaultMockConfiguration(1, CoffeeSize.DOUBLE, CoffeType.CAPUCCINO);
        Coffee doubleCoffee = machine.make(orderDouble);
        Assert.assertThat(doubleCoffee.getWaterAmount(), is(4));
    }

    @Test
    public void milkProviderShouldAddCorrectAmountOfMilkToCoffeeOfAllTypes() {
        int wantedMilkAmount = 2;

        for (CoffeType type: CoffeType.values()) {
            Coffee coffee = machine.make(createOrderAndSetDefaultMockConfiguration(wantedMilkAmount, CoffeeSize.SMALL, type));
            Assert.assertThat(coffee.getMilkAmout(), is(wantedMilkAmount));
        }
    }

    @Test
    public void coffeeWithoutMilkInReceiptShouldHaveNoMilk() {
        Coffee coffee = machine.make(createOrderAndSetDefaultMockConfiguration(0, CoffeeSize.SMALL, CoffeType.CAPUCCINO));
        Assert.assertThat(coffee.getMilkAmout(), is(0));
    }

    private CoffeOrder createOrderAndSetDefaultMockConfiguration(int milkAmount, CoffeeSize coffeeSize, CoffeType coffeType) {
        when(grinder.grind(coffeeSize)).thenReturn(true);
        CoffeeReceipe receipe = CoffeeReceipe.builder()
                                             .withMilkAmount(milkAmount)
                                             .withWaterAmounts(this.coffeeSizes)
                                             .build();
        when(receipes.getReceipe(coffeType)).thenReturn(receipe);

        return CoffeOrder.builder()
                         .withSize(coffeeSize)
                         .withType(coffeType)
                         .build();
    }
}