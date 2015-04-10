import groovy.sql.Sql
import static ratpack.spring.Spring.*

import javax.sql.DataSource

import static ratpack.groovy.Groovy.ratpack

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import ratpack.jackson.JacksonModule
import org.joda.time.LocalDate
import <%= baseName %>.models.*
import static ratpack.jackson.Jackson.json
import static ratpack.jackson.Jackson.jsonNode

ratpack {
  bindings {
    ObjectMapper om = new ObjectMapper()
    om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    om.registerModule(new JodaModule())
    bindInstance(ObjectMapper, om)
    add new JacksonModule()
  }
  handlers {
    register spring(<%= baseName %>.Application)

    <% _.each(entities, function (entity) { %>
    handler("<%= baseName %>/<%= pluralize(entity.name) %>") {
      byMethod {
        get {
          def <%= pluralize(entity.name) %>
          blocking {
            <%= pluralize(entity.name) %> = <%= _.capitalize(entity.name) %>.list()
          } then {
            render json(<%= pluralize(entity.name) %>)
          }
        }

        post {
          JsonNode node = parse(jsonNode())
          def <%= entity.name %> = new <%= _.capitalize(entity.name) %>(
            <% var delim = ''; _.each(entity.attrs, function (attr) { %>
            <%= delim %><%= attr.attrName %>: <% if (attr.attrType == 'Date') { %>new LocalDate(<% }; %>node.get("<%= attr.attrName %>").as<%= attr.attrJsonType %>()<% if (attr.attrType == 'Date') { %>)<% }; %><% delim = ', '; }); %>
          )
          blocking {
            <%= entity.name %>.save()
          } then {
            response.status(201)
            render json(<%= entity.name %>)
          }
        }
      }
    }

    handler("<%= baseName %>/<%= pluralize(entity.name) %>/:id") {
      byMethod {
        get {
          def <%= entity.name %>
          blocking {
            <%= entity.name %> = <%= _.capitalize(entity.name) %>.get(pathTokens.id)
          } then {
            render json(<%= entity.name %>)
          }
        }

        put {
          JsonNode node = parse(jsonNode())
          def <%= entity.name %>
          blocking {
            <%= entity.name %> = <%= _.capitalize(entity.name) %>.get(pathTokens.id)
          } then {
            if (!<%= entity.name %>) {
              response.status(404)
              render ""
            }
            <% _.each(entity.attrs, function (attr) { %>
            <%= entity.name %>.<%= attr.attrName %> = <% if (attr.attrType == 'Date') { %>new LocalDate(<% }; %>node.get("<%= attr.attrName %>").as<%= attr.attrJsonType %>()<% if (attr.attrType == 'Date') { %>)<% }; %><% }); %>
            blocking {
              <%= entity.name %>.save()
            } then {
              render json(<%= entity.name %>)
            }
          }
        }

        delete {
          def <%= entity.name %>
          blocking {
            <%= entity.name %> = <%= _.capitalize(entity.name) %>.get(pathTokens.id)
          } then {
            if (!<%= entity.name %>) {
              response.status(404)
              render ""
            }
            blocking {
              <%= entity.name %>.delete()
            } then {
              response.status(204)
              render ""
            }
          }
        }
      }
    }
    <% }); %>

    assets "public", "index.html"
  }
}
