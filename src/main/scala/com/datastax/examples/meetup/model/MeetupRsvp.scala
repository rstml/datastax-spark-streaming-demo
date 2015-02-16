package com.datastax.examples.meetup.model

/**
 * Created by rustam on 21/12/2014.
 */
case class MeetupEvent(
    event_id: String,
    event_name: Option[String],
    event_url: Option[String],
    time: Option[Long]
)

case class MeetupGroupTopics(
    topic_name: Option[String],
    urlkey: Option[String]
)

case class MeetupGroup(
    group_id: Long,
    group_name: String,
    group_city: Option[String],
    group_country: Option[String],
    group_state: Option[String],
    group_urlname: Option[String],
    group_lat: Option[String],
    group_lon: Option[String],
    group_topics: List[MeetupGroupTopics]
)

case class MeetupMember(
    member_id: Long,
    member_name: Option[String],
    other_services: Option[String],
    photo: Option[String]
)

case class MeetupVenue(
    venue_id: Long,
    venue_name: Option[String],
    lat: Option[String],
    lon: Option[String]
)

case class MeetupRsvp(
    rsvp_id: Long,
    response: String,
    guests: Int,
    mtime: Long,
    visibility : String,
    event: MeetupEvent,
    group: MeetupGroup,
    member: MeetupMember,
    venue: MeetupVenue
)