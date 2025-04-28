# Mock WhatsApp Application Using Akka Actors

## Overview

This is a mock WhatsApp application built using Akka actors to simulate a messaging system. The application includes various features such as:

- **Private Messaging**
- **Group Chats**
- **Community Conversations**
- **User Status Updates**

The system is organized into actors that handle core business logic, including message routing, private and group messaging, community chat management, user status updates, and session management.

## Components

The application is structured with the following key actors:

- **ServerActor**: Responsible for message routing across the system.
- **ChatManagerActor**: Manages direct/private messaging between users.
- **GroupChatManagerActor**: Handles group communication and management.
- **CommunityChatManagerActor**: Manages public community chat conversations.
- **StatusUpdateManagerActor**: Manages user status updates.
- **UserActor**: Manages user sessions, including login and authentication.


## Getting Started

To run the project, follow these steps:

1. Clone the repository.
2. Set up the required dependencies using Maven.
3. Start the server-side system.
4. Run the user client to interact with the messaging system.
