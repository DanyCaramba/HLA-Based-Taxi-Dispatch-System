# 🚖 HLA-Based Distributed Simulation of Multi-Zone Taxi Dispatch System

This project implements a distributed simulation of a taxi dispatch system using the **High-Level Architecture (HLA)** standard. Multiple taxi zones are modeled as federates, each with its own queue and passenger arrival behavior. The simulation aims to optimize taxi allocation and estimate ride throughput while avoiding passenger queue buildup.

---

## 🎯 Project Goals

- Implement HLA-based simulation with multiple federates representing taxi zones
- Simulate random passenger arrivals with different statistical distributions per zone
- Model queues of waiting taxis and passengers at each zone
- Assign taxis to passengers and simulate trips between zones
- Evaluate system performance: number of rides, wait times, and optimal fleet size

---

## 🧰 Technologies

- **Language**: Java  
- **Architecture**: HLA (High-Level Architecture for distributed simulation)  
- **RTI**: Portico / Pitch / other compatible HLA RTI  
- **Simulation Model**: Event-based, time-stepped

---

## 🧠 Federates Overview

- **Zone Federate**: Handles passenger arrival events and taxi queues  
- **Taxi Federate**: Represents individual taxis and manages their state transitions  
- **Coordinator Federate**: Optional – collects statistics, manages synchronization and end conditions

---

## 📊 Output Metrics

- Total number of completed rides per day  
- Passenger wait time per zone  
- Fleet utilization rate  
- Recommended minimum number of taxis to avoid waiting queues
