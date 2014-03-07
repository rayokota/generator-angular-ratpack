@Grab("io.ratpack:ratpack-groovy:0.9.2")
@Grab("io.ratpack:ratpack-jackson:0.9.2")
@Grab("com.fasterxml.jackson.datatype:jackson-datatype-joda:2.3.2")
@Grab("joda-time:joda-time:2.3")
@Grab("org.jadira.usertype:usertype.core:3.0.0.GA")
@Grab("org.grails:grails-datastore-gorm-hibernate4:3.0.0.RELEASE")
@Grab("org.grails:grails-spring:2.3.7")
@Grab("org.apache.derby:derby:10.10.1.1")
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import grails.orm.bootstrap.*
import grails.persistence.*
import org.apache.derby.jdbc.EmbeddedDriver
import org.joda.time.LocalDate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import ratpack.jackson.JacksonModule
import models.*
import static ratpack.groovy.Groovy.*
import static ratpack.jackson.Jackson.json
import static ratpack.jackson.Jackson.jsonNode

init = new HibernateDatastoreSpringInitializer("models")
def dataSource = new DriverManagerDataSource(EmbeddedDriver.name, "jdbc:derby:mydb;create=true", '', '')
init.configureForDataSource(dataSource)

ratpack {
  modules {
    bind ObjectMapper, new ObjectMapper()
    register new JacksonModule()
    init { ObjectMapper om ->
      om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      om.registerModule(new JodaModule())
    }
  }
  handlers {
    <% _.each(entities, function (entity) { %>
    handler("<%= baseName %>/<%= pluralize(entity.name) %>") {
      byMethod {
        get {
          render json(<%= _.capitalize(entity.name) %>.list())
        }

        post {
          JsonNode node = parse(jsonNode())
          def <%= entity.name %> = new <%= _.capitalize(entity.name) %>(
            <% var delim = ''; _.each(entity.attrs, function (attr) { %>
            <%= delim %><%= attr.attrName %>: <% if (attr.attrType == 'Date') { %>new LocalDate(<% }; %>node.get("<%= attr.attrName %>").as<%= attr.attrJsonType %>()<% if (attr.attrType == 'Date') { %>)<% }; %><% delim = ', '; }); %>
          )
          <%= entity.name %>.save()
          response.status(201)
          render json(<%= entity.name %>)
        }
      }
    }

    handler("<%= baseName %>/<%= pluralize(entity.name) %>/:id") {
      byMethod {
        get {
          render json(<%= _.capitalize(entity.name) %>.get(pathTokens.id))
        }

        put {
          JsonNode node = parse(jsonNode())
          def <%= entity.name %> = <%= _.capitalize(entity.name) %>.get(pathTokens.id)
          if (!<%= entity.name %>) {
            response.status(404)
            render ""
          }
          <% _.each(entity.attrs, function (attr) { %>
          <%= entity.name %>.<%= attr.attrName %> = <% if (attr.attrType == 'Date') { %>new LocalDate(<% }; %>node.get("<%= attr.attrName %>").as<%= attr.attrJsonType %>()<% if (attr.attrType == 'Date') { %>)<% }; %><% }); %>
          <%= entity.name %>.save()
          render json(<%= entity.name %>)
        }

        delete {
          def <%= entity.name %> = <%= _.capitalize(entity.name) %>.get(pathTokens.id)
          if (!<%= entity.name %>) {
            response.status(404)
            render ""
          }
          <%= entity.name %>.delete()
          response.status(204)
          render ""
        }
      }
    }
    <% }); %>

    assets "public"
  }
}
