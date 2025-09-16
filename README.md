
# Device API (Network device API)

Network deployment might consist of several devices.
Networking device might be of following types:

• Gateway - serves as access point to another network

• Switch - connects devices on a computer network

• Access Point - connects devices on a computer network via Wi-Fi

Typically, these devices are connected to one another and collectively form a
network deployment.
Every device on a computer network can be identified by MAC address.
If device is attached to another device in same network, it is represented via
uplink reference.


## API Reference

#### Retrieve all registered devices, sorted by device type (sorting order: Gateway > Switch > Access Point)

```http
  GET /devices
```


#### Retrieve network deployment device by MAC address

```http
  GET /devices/${macAddress}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `macAddress`      | `string` | **Required**. macAddress of device to fetch |

#### Retrieve all registered network device topology


```http
  GET /topology
```

#### Retrieve network device topology starting from a specific device

```http
  GET /topology/${macAddress}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `macAddress`      | `string` | **Required**. macAddress of device to fetch |

#### Register a device to a network deployment

```http
  POST /devices/register
```

| Parameter | Type     | Description                       |
 :-------- | :------- | :-------------------------------- |
| `deviceType`      | `string` | **Required**. type of device 
| `macAddress`      | `string` | **Required**. macAddress of device |
| `uplinkMacAddress`      | `string` |  macAddress of device attached to 

#### Update a device in a network deployment.  
If device type is not set that it will be not updated. If uplink MAC is not set it will be set to null.

```http
  POST /devices/update
```

| Parameter | Type     | Description                       |
 :-------- | :------- | :-------------------------------- |
| `deviceType`      | `string` | type of device 
| `macAddress`      | `string` | **Required**. macAddress of device |
| `uplinkMacAddress`      | `string` |  macAddress of device attached to 


#### Delete a device from a network deployment

```http
  DELETE /devices/delete/${macAddress}
```

| Parameter | Type     | Description                       |
 :-------- | :------- | :-------------------------------- |
| `macAddress`      | `string` | **Required**. macAddress of device |

## Run Locally

Clone the project

```bash
  git clone https://github.com/ryzhyjBy/deviceApi.git
```

Go to the project directory

```bash
  cd deviceApi/
```

Start the server

```bash
  ./gradlew bootRun
```


## Running Tests

To run tests, run the following command

```bash
  ./gradlew test
```


## Usage/Examples

```
Please refer to swagger page http://localhost:8080/swagger-ui/index.html
```


## Schemas
Device
| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `deviceType`      | `string` | Type of device. Allowed values: `GATEWAY`, `SWITCH`, `ACCESS_POINT` |
| `macAddress`      | `string` | macAddress of device|
| `uplinkMacAddress`      | `string` | macAddress of device attached to |


