import ReactDOM from "react-dom";
import Dialog from "../../../components/controls/Dialog";

jest.mock("react-redux", () => ({
  useSelector: jest.fn((fn) => fn()),
}));

const buttons = {
  label: "testBtn",
  onClick: () => {
    console.log("button click is working");
  },
};

const dialogOpen = false;
const dialogButtons = [{ label: "Cancel" }, { label: "Delete" }];

it("render dialog without crashing", () => {
  const div = document.createElement("div");
  ReactDOM.render(
    <Dialog open={dialogOpen} buttons={dialogButtons} title={"testTitle"}></Dialog>,
    div
  );
});