# Lab2SysArchitectures

# How to start the Application:
1. Open in IntelliJ or a similar Program
2. Click Run on the top Bar, or if that is not an option try running the main class "HomeAutomationSystem.java instead"

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

Important Sidenote: The commands have to be entered in lower caps!

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
Should an item be out of stock after consuming it, a new one will be tried to order <br>
If there is no space for the ordered item, the order will be canceled