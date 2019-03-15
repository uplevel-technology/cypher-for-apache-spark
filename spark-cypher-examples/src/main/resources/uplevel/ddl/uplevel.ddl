-- noinspection SqlDialectInspectionForFile

SET SCHEMA H2.RESOLUTION_DATALAKE;

CREATE GRAPH Resolutions (

  -- nodes
  DomainName ( value STRING ),

  IpAddress ( value STRING, version STRING ),

  InfoSource ( id INTEGER, name STRING ),

  Resolution (id INTEGER ),

  -- relationships
  RESOLVED_TO(
    timestamp STRING -- actually can be date
  ),

  -- node mappings
  (DomainName)  -- references DomainName node type defined above
    FROM DomainName,  -- references DomainName table

  (IpAddress)
    FROM IpAddress,

  (InfoSource)
    FROM InfoSource,



  -- rel mappings
  (DomainName)-[RESOLVED_TO]->(IpAddress)
    FROM Resolution resolution
      START NODES (DomainName)
        FROM DomainName domainName
          JOIN ON domainName.VALUE = resolution.DOMAINNAME
      END NODES (IPAddress)
        FROM IpAddress ip
          JOIN ON ip.VALUE = resolution.RESOLVESTO

)