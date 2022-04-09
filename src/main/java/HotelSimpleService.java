import java.util.*;

public class HotelSimpleService {
    public static void main(String[] args) {
        HotelManagement hotel = new HotelManagement();
        hotel.Process();
    }
}

class HotelManagement{
    private Room[] rooms;
    public void Process(){
        Scanner readInput = new Scanner(System.in);
        ShowCommandHelp();
        do {
            String strInput = "";
            strInput += readInput.nextLine();
            String[] option = strInput.split(" ");
            switch (option[0].toLowerCase()){
                case "create":
                    if (ValidateCreateRoom(option) && option[1].toLowerCase().equals("room"))
                        CreateRoom(option[2]);
                    break;
                case "book":
                    if (ValidateBooking(option))
                        try{
                            Booking(Integer.parseInt(option[1]), Integer.parseInt(option[2]), Integer.parseInt(option[3]));
                        }
                        catch (Exception e){
                            System.err.println("Did you input wrong room id?");
                        }
                    break;
                case "cancel":
                    if (ValidateCancelBook(option))
                        try{
                            CancelBook(Integer.parseInt(option[1]), Integer.parseInt(option[2]));
                        }
                        catch (Exception e){
                            System.err.println("Did you input wrong room id or book id?");
                        }
                case "look":
                    GetRoomsWithBooks();
                    break;
                default:
                    ShowWrongInput();
                    break;
            }
        } while (true);
    }

    private void GetRoomsWithBooks() {
        System.out.println(
                rooms == null ?
                        "No room now. Please create room at least 1." :
                        Arrays.toString(rooms).substring(1,Arrays.toString(rooms).length()-1));
    }

    private void CreateRoom(String roomName){
        Room newRoom = new Room(rooms == null ? 1 : rooms.length+1, roomName);
        AddRoom(newRoom);
        System.out.println(newRoom);
    }
    private void Booking(int roomId, int checkInDate, int checkOutDate){
        String bookResult = rooms[roomId-1].BookRoom(checkInDate, checkOutDate);
        System.out.println(bookResult);
    }
    private void CancelBook(int roomId, int bookId){
        rooms[roomId-1].RemoveBookByBookId(bookId);
        System.out.println("Remove success.");
    }
    private void AddRoom(Room newRoom) {
        if (rooms == null)
            rooms = new Room[]{newRoom};
        else {
            List<Room> roomBase = new ArrayList<Room>(Arrays.asList(rooms));
            roomBase.add(newRoom);
            rooms = roomBase.toArray(rooms);
        }
    }
    private void ShowWrongInput(){
        System.out.print("Wrong input. ");
        ShowCommandHelp();
    }
    private void ShowCommandHelp(){
        System.out.println("Please command..");
        System.out.println(" create room <room-name>");
        System.out.println(" book <room-id> <check-in-date> <check-out-date>");
        System.out.println(" cancel <room-id> <book-id>");
        System.out.println(" look : to see all room and booking list of each");
    }
    private boolean ValidateCreateRoom(String[] option){
        if (option.length < 3) {
            System.err.println("Invalid to create room. Please command 'create room <room-name>'");
            return false;
        }
        return true;
    }
    private boolean ValidateBooking(String[] option){
        if (option.length < 4){
            System.err.println("Invalid to booking room. Please command 'book <room-id> <check-in-date> <check-out-date>'");
        }
        else {
            try{
                Integer.parseInt(option[1]);
                int checkInDate = Integer.parseInt(option[2]);
                int checkOutDate = Integer.parseInt(option[3]);
                if (checkInDate < checkOutDate)
                    return true;
                System.err.println("Invalid to booking room. Please recheck your check in and check out date again.");
            }
            catch (Exception e){
                System.err.println("Wrong argument type. Please command 'book <room-id> <check-in-date> <check-out-date>'");
            }
        }
        return false;
    }
    private boolean ValidateCancelBook(String[] option){
        if (option.length < 3){
            System.err.println("Invalid to booking room. Please command 'cancel <room-id> <book-id>'");
        }
        else {
            try{
                Integer.parseInt(option[1]);
                Integer.parseInt(option[2]);
                return true;
            }
            catch (Exception e){
                System.err.println("Wrong argument type. Please command 'cancel <room-id> <book-id>'");
            }
        }
        return false;
    }
}
class Room{
    private int id;
    private String roomName;
    private Book[] bookList;

    public Room(int id, String roomName) {
        this.id = id;
        this.roomName = roomName;
    }

    public int getId() { return id; }
    public String getRoomName() { return roomName; }
    public Book[] getBookList() { return bookList; }

    public String BookRoom(int checkInDate, int checkOutDate){
        String stringReturn;
        Book newBook = new Book(bookList == null ? 1: bookList[bookList.length-1].getId()+1, checkInDate, checkOutDate);
        boolean bookAccepted = bookList == null ? true:(IsCanBook(checkInDate, checkOutDate));
        if (bookAccepted) {
            AddBook(newBook);
            stringReturn = StringBookSuccess(newBook);
        } else {
            stringReturn = "Room not available.\n" + Arrays.toString(bookList);
        }
        return stringReturn;
    }
    public Book[] RemoveBookByBookId(int bookId){
        List<Book> books = new ArrayList<Book>(Arrays.stream(bookList).filter(a->a.getId()!=bookId).toList());
        bookList = books.toArray(new Book[bookList.length-1]);
        return bookList;
    }
    private boolean IsCanBook(int checkInDate, int checkOutDate){
        boolean bookAccepted = true;
        for (Book book:bookList) {
            if (!((checkInDate >= book.getCheckOutDate() || checkOutDate <= book.getCheckInDate()) && checkInDate<checkOutDate)){
                bookAccepted = false;
                break;
            }
        }
        return bookAccepted;
    }
    private void AddBook(Book newBook){
        if (bookList == null)
            bookList = new Book[]{newBook};
        else {
            List<Book> booksBase = new ArrayList<Book>(Arrays.asList(bookList));
            booksBase.add(newBook);
            bookList = booksBase.toArray(bookList);
        }
    }
    private String StringBookSuccess(Book newBook){
        return  "Booking success!\n" + newBook.toString();
    }
    @Override
    public String toString() {
        return "\nRoomId: " + id + " roomName: '" + roomName + '\'' + "\nBookList:\n " +
                ((bookList == null) ?
                        "" :
                        Arrays.toString(bookList)
                              .replace(',','\n')
                              .substring(1, Arrays.toString(bookList).length()-1)) + "\n";
    }
}
class Book{
    private int id;
    private int checkInDate;
    private int checkOutDate;

    public Book(int id, int checkInDate, int checkOutDate) {
        this.id = id;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }
    public int getId() { return id; }
    public int getCheckInDate() { return checkInDate; }
    public int getCheckOutDate() { return checkOutDate; }

    @Override
    public String toString() {
        return "BookId: " +id+ " [" +checkInDate+ " -> " +checkOutDate +"]";
    }
}