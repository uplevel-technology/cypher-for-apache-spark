-- noinspection SqlDialectInspectionForFile

SET SCHEMA H2.DNS_RESOLUTION;

CREATE GRAPH DnsHistory (

  Domain ( value STRING ),                               -- domain node declaration
  Ip ( value STRING ),                                   -- ip node declaration
  RESOLVED_TO( timestamp localdatetime),                 -- RESOLVED_TO relationship declaration

  (Domain) FROM DNS_RESOLUTION (DomainName as value),   -- mapping to domain nodes from sql
  (Ip) FROM DNS_RESOLUTION (IPAddress as value),        -- mapping to IP nodes from sql


  (Domain)-[RESOLVED_TO]->(Ip) FROM DNS_RESOLUTION rel  -- mapping to RESOLVED_TO rels from sql
    START NODES (Domain) FROM DNS_RESOLUTION row JOIN ON rel.DomainName = row.DomainName
    END NODES (Ip) FROM DNS_RESOLUTION row JOIN ON rel.IPAddress = row.IPAddress

)

