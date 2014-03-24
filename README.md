EasyTab
==========
Team 9 - "Team Lead Develop" CS4911 Capstone Project

Vision :

When discussing everyday problems amongst our group, we came to several wild ideas. We wanted to create smart shopping carts that allowed users to pay via PayPal. There was also an idea about using a raspberry pi to start a car from a far because of the harsh cold weather. But in the end, we came to the idea of a smart bill splitting app. The problem stemmed from trouble in splitting a bill when in a large group. We came up with three individual issues around bill splitting. One issue was the waiter would get confused as to which ways to split causing overcharges. Another problem is when the waiter has to deal with POS constraints. For instance, the POS would allow bills to be split a maximum of three ways. The final scenario is when a group decides one person pay the entire bill and they do not get paid back. Bill splitting is both a problem for retail locations and for customers. We want to fix that.
 
To solve these problems we have come up with an end-to-solution that helps both customer and seller. It features a single tablet that sits at every table. Each table has a number, and each tablet is preconfigured with our software and their table number. When it’s time to pay, the people in the group can sift through what they bought and choose what they want to pay for. For each bill, the customer will enter in their cell phone number and get a bill url texted to them. On this page users will see options to pay will PayPal, Google Wallet, Cash, or a Credit Card.

This solution makes it easier for both waiters and customers. For the waiters, they will be able to easily assign purchases to a table. Also, they will no longer have to run between tables to handle receipts, credit cards, and cash. And mostly, the app will benefit the customer. They will be able to drag and drop purchased menu items and pay right at the table.
==========
General Information:

EasyTab is broken into two platforms. The WebPlatform is a responsive webpage designed for company front-facing interactions. From the WebPlatform, a restaurant owner may create a menu, view open/closed orders, and change account settings. It is driven through Parse database interactions created through JavaScript. See https://parse.com/docs/js_guide for full documentation for Parse using JavaScript. The AndroidPlatform is used for customer interactions. EasyTab is a tablet application that enables users to split items and the table's tab any which way. Once the table has agreed on how bill shall be split, customers are sent text messages (by means of Twilio) with a PayPal link setup to pay the restaurant. 
==========
Implementation:

_Web Platform_

Parse is initialized at the top of each javascript file. An application ID and a javascript key are passed as arguments.

```
Parse.initialize("_applicationID_", "_javascript key_");
```
