# Lab2SysArchitectures

# How to start the Application:
1. Open in IntelliJ or a similar Program
2. Click Run on the top Bar, or if that is not an option try running the main class "HomeAutomationSystem.java" instead

# How to interact with the Application:
This application is a commandline Application, meaning that the entire interaction is done within the commandline, in this case the IntelliJ internal commandline.

It understands a variety of different commands, below is a list of which ones may be used.

### Media Station:
    mediastation true - turns the mediastation on
    mediastation false - turns the mediastation off

### Temperature Sensor:
    temperature {value} - sets the current temperature to the one entered.

### Weather Sensor:
    weather {weather} - sets the current weather to the entered value

### Air Conditioner:
    ac on - turns the ac on (but does not activate it)
    ac off - turns the ac off

### Fridge:
    consume {productName} - will remove the given item from the fridge if it is available
    order {productPrice} {productWeight} {productName} {Optional: Amount} - will try to order given product
    store {weight} {any price} {name} - stores item (There is no cap on the weight on this, so only for debugging uses!, otherwise use the order function
Important Side note: The commands have to be entered in lower caps!

## A few rules which the application follows:

### Temperature Simulator:
Changes the temperature by a random amount every 30 seconds.

### Weather Simulator:
Changes the weather every 60 seconds randomly to one of the 5 predefined weather types.

### Blinds:
Will close when either media is playing or the weather is sunny. <br>
Will also only open when no media is playing AND the weather is not sunny.

### Air Conditioner:
Will only activate if it is turned on AND the temperature is above 20. <br>
Automatically turns off if the temperature goes below 20.

### Fridge:
The fridge can only hold a maximum of 50 items and 50 KGs at a time <br>
Items can be consumed from the fridge <br>
Should an item be out of stock after consuming it, a new one will be tried to order <br>
If there is no space for the ordered item, the order will be canceled <br>
Items can be stored, but this is usually only used on orders currently

# Test Scenarios:
### Turning the AC on and off:
    ac off
    temperature 20+ - To test if its working correctly, should not work if it is working as intended
    ac on

### Activating the AC:
    temperature 20+ - should cause the AC to activate

### Deactivating the AC:
    temperature 19- - should cause the AC to deactivate

### Opening/Closing Blinds:
    weather sunny - closes blinds
    weather {not sunny} - opens blinds
    mediastation true - closes blinds
    weather sunny - closes blinds (again)
    mediastation false - cannot open blinds -> still sunny
    mediastation true 
    weather {not sunny} - cannot open blinds -> media playing
    mediastation false - opening blinds

### Consuming items from the fridge:
    consume eggs - will remove the eggs from the fridge & immediately order new ones
    consume eggs - cannot consume - none left

### Ordering items:
    order {50+} {any price} {name} - will not work, not enough space left
    order {0 - 50} {any price} {name} - will order the item, if there arent 50 items yet

### Displaying Contents
    fridgecontents - will display eggs as standard, as there are no other dummy data in there, but will display more as more get added!
