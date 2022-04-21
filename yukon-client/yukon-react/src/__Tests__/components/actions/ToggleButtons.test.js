import { ToggleButton } from "@material-ui/lab";
import ReactDOM from "react-dom";

jest.mock("react-redux", () => ({
  useSelector: jest.fn((fn) => fn()),
}));

it("render Toggle Button without crashing", () => {
  const div = document.createElement("div");
  ReactDOM.render(
    <ToggleButton value={true} name="enabled" label="Status"></ToggleButton>,
    div
  );
});