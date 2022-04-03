import Badge from "react-bootstrap/Badge";
import Col from "react-bootstrap/Col";
import Container from "react-bootstrap/Container";
import Figure from "react-bootstrap/Figure";
import ListGroup from "react-bootstrap/ListGroup";
import React, { Component, useEffect, useState } from "react";
import Row from "react-bootstrap/Row";
import { TwitterTweetEmbed } from "react-twitter-embed";
import Card from 'react-bootstrap/Card';

class TweetList extends Component {
  setSentiment(str) {
    return (str == "Positive" ? "primary" : "danger");
  }

  constructor(props) {
    super(props);
    this.state = {
      tweets: [
        { id: 1, text: "a", sentiment: "Positive" }
      ]
    };
    this.remove = this.remove.bind(this);
  }

  componentDidMount() {
    fetch('/tweets')
      .then(response => response.json())
      .then(data => this.setState({ tweets: data }));
  }

  componentDidUpdate() {
    fetch('/tweets')
      .then(response => response.json())
      .then(data => this.setState({ tweets: data }));
  }

  async remove(id) {
    await fetch(`/tweets/${id}`, {
      method: 'DELETE',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      }
    }).then(() => {
      let updatedTweets = [...this.state.tweets].filter(i => i.id !== id);
      this.setState({ tweet: updatedTweets });
    })
  }



  render() {
    const { tweets, isLoading } = this.state;

    if (isLoading) {
      return <p>Loading...</p>
    }

    const tweetList = tweets.map(tweet => {
      return <div className="px-3 py-2 " key={tweet.id}>
        <div className={"" + (tweet.sentiment == "Positive" ? "z-glow-blue" : "z-glow-red")}>
          <TwitterTweetEmbed
            onLoad={function noRefCheck() { }}
            options={{
              cards: 'hidden',
              hideCard: true,
              hideThread: false,
              maxWidth: 1000,
              width: 300
            }}
            tweetId={tweet.tweetId}
          />
          <Card className="z-margin" style={{ width: '18rem' }}>
            <Card.Body>
              <Card.Title><div className={"" + (tweet.sentiment == "Positive" ? "z-pos-emoji" : "z-neg-emoji")}></div></Card.Title>
              <Card.Subtitle className="mb-1 text-muted">Score: {tweet.score}</Card.Subtitle>
            </Card.Body>
          </Card>
        </div>
      </div>
      {/* <div>{tweet.text}</div>
        <Badge bg={this.setSentiment(tweet.sentiment)} pill>
          {tweet.sentiment}
        </Badge> */}
    });

    return (
      <Container>
        <Row className="justify-content-center">
          {/* <Col lg="1">1 of 3</Col> */}
          <Col lg="10" className="d-flex flex-wrap">
            {tweetList}
            {/* <div className="px-3 py-2 ">
              <div className="z-glow-red">
                <TwitterTweetEmbed
                  onLoad={function noRefCheck() { }}
                  options={{
                    cards: 'hidden',
                    hideCard: true,
                    hideThread: false,
                    maxWidth: 1000,
                    width: 300
                  }}
                  tweetId="1509130847936745475"
                />
                <Card className="z-margin" style={{ width: '18rem' }}>
                  <Card.Body>
                    <Card.Title><div className="z-neg-emoji"></div></Card.Title>
                    <Card.Subtitle className="mb-1 text-muted">Score: 0.7</Card.Subtitle>
                  </Card.Body>
                </Card>
              </div>
            </div>
            <div className="px-3 py-2 ">
              <div className="z-glow-blue">
                <TwitterTweetEmbed
                  onLoad={function noRefCheck() { }}
                  options={{
                    cards: 'hidden',
                    hideCard: true,
                    hideThread: false,
                    maxWidth: 1000,
                    width: 300
                  }}
                  tweetId="1509130214622003203"
                />
              </div>
            </div><div className="px-3 py-2 " >
              <div className="z-glow-blue">
                <TwitterTweetEmbed
                  onLoad={function noRefCheck() { }}
                  options={{
                    cards: 'hidden',
                    hideCard: true,
                    hideThread: false,
                    maxWidth: 1000,
                    width: 300
                  }}
                  tweetId="1509115325496799235"
                />
              </div>
            </div><div className="px-3 py-2 ">
              <div className="z-glow-blue">
                <TwitterTweetEmbed
                  onLoad={function noRefCheck() { }}
                  options={{
                    cards: 'hidden',
                    hideCard: true,
                    hideThread: false,
                    maxWidth: 1000,
                    width: 300
                  }}
                  tweetId="1509113736371802113"
                />
              </div>
            </div> */}
            {/* <ListGroup.Item>
                <div>Lorem ipsum</div>
                <Badge bg="primary" pill>
                  Positive
                </Badge>
              </ListGroup.Item> */}
            {/* <ListGroup.Item>
                <div>Lorem ipsum</div>
                <Badge bg="secondary" pill>
                  Neutral
                </Badge>
              </ListGroup.Item>
              <ListGroup.Item>
                <div>Lorem ipsum dolor sit amet,</div>
              </ListGroup.Item>
              <ListGroup.Item>
                <div>
                  Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed
                  do eiusmod tempor incididunt ut labore et dolore magna aliqua.
                  Ut enim ad minim veniam, quis nostrud exercitation ullamco
                  laboris nisi ut aliquip ex ea commodo consequat. Duis aute
                  irure dolor in reprehenderit in voluptate velit esse cillum
                  dolore eu fugiat nulla pariatur.
                </div>
                <Badge bg="danger" pill>
                  Negative
                </Badge>
              </ListGroup.Item> */}
          </Col>
          <Col lg="2">
            <Figure>
              <Figure.Image
                width={500}
                height={500}
                alt="twitter-logo"
                src="https://theufuoma.com/wp-content/uploads/2017/06/twitter-icon-300x300.png"
              />
              {/* <Figure.Caption>Sentiment Analysis</Figure.Caption> */}
            </Figure>

            <div class="p-3 mb-2 bg-dark text-white">
              Tweet Sentiment Analysis
            </div>
          </Col>
        </Row>
      </Container>
    );
  }
}

export default TweetList;
