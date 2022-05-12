# Chosen Architecture & Patterns

The only overarching architecture chosen for this was the actor architecture within akka.
In Akka the actors interact with each other by sending a message to another, which create an object, which will cause the target actor to call a function in response.<br>

In our small projects we mostly used fire and forget, with which a message gets sent to the receiver and the sender forgets it happened right afterwards.
This happens for example with weather and temperature sensors, they send the weather and temperature to blinds and ac respectively and move on.
Used mostly because no responses are required & the actions can be resolved within 1 function.<br>

Another pattern used is the scheduling messages to self pattern, this one is used exclusively used by the 2 simulators, to schedule a randomised change
& a fire and forget message to the corresponding sensors.
This is very useful to simulators, as they always will call their own functions through a message to themselves at the same exact times. <br>

The Order Processor on the other hand is a temporary actor, summoned every time by the Fridge Controller and is destroyed right after it outlived its use.<br>
This processor also is capable of request and response. It requests the weight and space still available in the Fridge from the Weight and Amount sensors.<br>
This is used mostly because the processor is a temporary child actor to the Fridge Controller and through request response it is easy to pass on a reference to self.