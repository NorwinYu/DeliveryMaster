# DeliveryMaster Requirements

## Basic

**A food delivery agent application**

### Main Features

- let user (delivery agent) chooses possible orders
- generate daily statistics
- track the movement of the device over time
- log the change in physical location using GPS
- display the positions of the user

## Minimum Requirements

- Displaying **3 orders** to deliver **within 10 km** from his current position by **ascending order**
- Allowing the user to **select the order** to deliver and **see the pickup and delivery point**.
- Allowing the user to view its **current location** and **path since the start of a delivery**
- Allowing the user to have **daily statistics** such as the **number of orders taken**, **the distance covered,** **the money earned.**
- Allowing the user to **set a delivery price** e.g 8 RMB per delivery.
- Allowing the user to **set the status** of the order (**pending pickup, picked-up or delivered)**.

### Components Requirements

- Activity
- Service
- Content Provider
- Broadcast Receiver

### Environment

- **Nexus S** device 
- running Android API version **24**

### Further Additional Functionality Example

- **different interpretations of what it means to log movement**

### Document Requirements

- design and architecture
- a rationale for the components that you have implemented and their communication
- the behaviour of the application from the user’s point of view
- maximum length is 1000 words
- include a diagram showing the components and their relationships
- a short explanation of each one,
  - discrete Activity components
  - how and when Services are started
  - how data is abstracted from underlying storage

