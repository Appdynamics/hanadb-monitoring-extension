# AppDynamics HanaDB - Monitoring Extension

This extension works only with the standalone machine agent.

## Use Case

## Prerequisite

## Installation

1. Run `maven clean install`. Deploy the HanaDBMonitor-<VERSION>.zip file found in 'target' into the \<machine agent home\>/monitors directory.

  ```   
  > cd <machine agent home>/monitors/
  > unzip HanaDBMonitor-<VERSION>.zip
  ```

2. Set up config.yml:

  ```
  # HanaDB Host/IP and port.
  host: ""
  port: ""

  # Specify this key if Password Encryption Support is required. If not keep it empty
  # If specified, DBPassword is now the encrypted passwords.
  encryptionPassword: ""
  encryptionKey: ""

  # DB username and password.
  username: ""
  password: ""

  jdbcPrefix: "jdbc:sap://"
  jdbcOptions: "?communicationtimeout=2000"

  #prefix used to show up metrics in AppDynamics
  metricPathPrefix: "Custom Metrics|HanaDB"
  #This will create it in specific Tier. Replace <TIER_ID>
  #metricPrefix:  "Server|Component:<TIER_ID>|Custom Metrics|HanaDB Server|"
  # number of concurrent tasks
  numberOfThreads: 10

  queries:
    - statement: "select * from M_DISK_USAGE where USED_SIZE >= 0"
      columns:
       - name: "HOST"
         type: "name"
       - name: "USAGE_TYPE"
         type: "name"
       - name: "USED_SIZE"
         type: "metric"
         convertFrom: ""
         convertTo: ""
    - statement: "select HOST, USED_PHYSICAL_MEMORY, FREE_PHYSICAL_MEMORY from M_HOST_RESOURCE_UTILIZATION"
      columns:
       - name: "HOST"
         type: "name"
       - name: "USED_PHYSICAL_MEMORY"
         type: "metric"
         convertFrom: ""
         convertTo: ""
       - name: "FREE_PHYSICAL_MEMORY"
         type: "metric"
         convertFrom: ""
         convertTo: ""

  ```

3. Restart the Machine Agent.

## Directory Structure

<table><tbody>
<tr>
<th align = 'left'> Directory/File </th>
<th align = 'left'> Description </th>
</tr>
<tr>
<td class='confluenceTd'> src/main/resources/config </td>
<td class='confluenceTd'> Contains monitor.xml and config.yml</td>
</tr>
<tr>
<td class='confluenceTd'> src/main/java </td>
<td class='confluenceTd'> Contains source code to the HanaDB monitoring extension </td>
</tr>
<tr>
<td class='confluenceTd'> target </td>
<td class='confluenceTd'> Only obtained when using maven. Run 'maven clean install' to get the distributable .zip file. </td>
</tr>
<tr>
<td class='confluenceTd'> pom.xml </td>
<td class='confluenceTd'> maven build script to package the project (required only if changing Java code) </td>
</tr>
</tbody>
</table>


## Metrics

#### config.yml

## Custom Metrics

## Contributing

Always feel free to fork and contribute any changes directly via [GitHub](https://github.com/michaelenglert/hanadb-monitoring-extension).

## Community

## Support

For any questions or feature request, please contact [AppDynamics Center of Excellence](mailto:help@appdynamics.com).
