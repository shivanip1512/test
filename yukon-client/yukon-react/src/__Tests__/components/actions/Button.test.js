import React from "react";
import ReactDOM from "react-dom";
import Button from "../../../components/controls/Button";

jest.mock("react-redux", () => ({
  useSelector: jest.fn((fn) => fn()),
}));

it("render button without crashing", () => {
  const div = document.createElement("div");
  ReactDOM.render(<Button></Button>, div);
});