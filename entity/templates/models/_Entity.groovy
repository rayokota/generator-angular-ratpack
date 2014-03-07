package models

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility
import com.fasterxml.jackson.annotation.JsonProperty
import grails.persistence.*
import org.hibernate.type.EnumType
import org.joda.time.LocalDate
import org.jadira.usertype.dateandtime.joda.PersistentLocalDate

@Entity
@JsonAutoDetect(getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
class <%= _.capitalize(name) %> {
  @JsonProperty int id
  <% _.each(attrs, function (attr) { %>
  @JsonProperty <% if (attr.attrType == 'Enum') { %><%= _.capitalize(attr.attrName) %><% } else if (attr.attrType == 'Date') { %>Local<% }; %><%= attr.attrType %> <%= attr.attrName %><% }); %>
  static constraints = {
    <% _.each(attrs, function (attr) { %>
    <%= attr.attrName %> <% if (attr.maxLength) { %><% if (attr.minLength) { %>size: <%= attr.minLength %>..<%= attr.maxLength %>, <% } else { %>maxSize: <%= attr.maxLength %>, <% }}; %><% if (attr.max) { %><% if (attr.min) { %>range: <%= attr.min%><%= attr.attrType.charAt(0) %>..<%= attr.max%><%= attr.attrType.charAt(0) %>, <% } else { %>max: <%= attr.max %><%= attr.attrType.charAt(0) %>, <% }}; %>nullable: <% if (attr.required) { %>false<% } else { %>true<% }; %><% }); %>
  }
  static mapping = {
    table '<%= pluralize(name) %>'
    version false
    autoTimestamp false
    <% _.each(attrs, function (attr) { %>
    <% if (attr.attrType == 'Date') { %><%= attr.attrName %> type: PersistentLocalDate <% }; %><% }); %>
  }
}
