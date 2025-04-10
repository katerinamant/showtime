package com.example.showtime.FaqPage;

import com.example.showtime.Utils.Faq;

import java.util.ArrayList;
import java.util.List;

public class FaqPresenter {
    public List<Faq> getFaqCategory(int faqCategoryId) {
        Faq faq;
        ArrayList<Faq> res = new ArrayList<>();

        switch (faqCategoryId) {
            case 1:
                // Booking & Confirmation

                faq = new Faq();
                faq.setQuestion("How do I book a ticket?");
                faq.setAnswer("Simply tell UsherBot which show and date you’d like (e.g., “Book 2 tickets for Saturday’s show”). It will confirm availability and ask for your name and phone number.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("Can I select specific seats?");
                faq.setAnswer("You can choose a seating section (red, yellow, blue, or green -accessible seating), but individual seat numbers aren’t currently available. UsherBot reserves your spot in the chosen section.\n\n" +
                        "Red seats are right upfront and yellow seats are right behind them. Blue seats are on the sides, in rows of three and are slightly elevated.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("Do I have to keep my reservation code?");
                faq.setAnswer("Yes! Once your booking is complete, you’ll see a unique code in the chat. Keep it handy for any changes, cancellations, or refunds.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("I need accessible seating. Can UsherBot help me?");
                faq.setAnswer("Absolutely. Just let UsherBot know you need accessible seating, and it will reserve your seats in the designated seating area -one seat for you and one for a companion");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("Why do I have to give my phone number?");
                faq.setAnswer("We use your phone number to confirm your identity, avoid duplicate bookings, and help you recover or change a reservation if you lose your code.");
                res.add(faq);
                break;

            case 2:
                // Pricing & Payment

                faq = new Faq();
                faq.setQuestion("What are the ticket prices?");
                faq.setAnswer("Red seats are right upfront and cost 40€ each.\n\n" +
                        "Yellow seats are right behind them -these cost 30€.\n\n" +
                        "Blue seats are on the sides and are slightly elevated -these cost 25€ each.\n\n" +
                        "Accessible seating is in a designated part of the red section and these cost 25€ each.\n\n"+
                        "Feel free to ask UsherBot for more details and it will confirm the exact price when booking.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("Can I pay online or only at the booth?");
                faq.setAnswer("Currently, you’ll pay at the theater’s ticket booth. Show your reservation code when you arrive and our staff will handle the payment. We plan to add online payment soon!");
                res.add(faq);
                break;

            case 3:
                // Managing my booking

                faq = new Faq();
                faq.setQuestion("How do I change my booking?");
                faq.setAnswer("Give UsherBot your reservation code and let it know the new show date or time you’d prefer. If seats are available, it updates your booking right away.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("What if I typed the wrong date?");
                faq.setAnswer("If it’s not the same day of the show, simply provide UsherBot your reservation code and correct date/time.\n\n" +
                        "If it’s already show day, you’ll need to contact support.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("What if I need to cancel last-minute?");
                faq.setAnswer("Same-day cancellations aren’t allowed through UsherBot. If you have an urgent situation, please call our support line or visit the booth in person.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("What if I forget my reservation code?");
                faq.setAnswer("Provide UsherBot with the phone number you used when booking. If it still can’t find your code or you don't have the phone number, you may need to call our support line for further assistance.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("How do I rate my show?");
                faq.setAnswer("After your show, ask UsherBot to show you a 0–5 star scale. Just pick the star you want and confirm.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("Can I edit my rating after submitting?");
                faq.setAnswer("Once you confirm your star rating, it’s final—so choose carefully.");
                res.add(faq);
                break;

            case 4:
                // Show Information

                faq = new Faq();
                faq.setQuestion("Which days are shows playing?");
                faq.setAnswer("We typically run shows Tuesday through Sunday (afternoon and evening). Mondays are our rest day.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("What times are the shows?");
                faq.setAnswer("We do multiple shows everyday!\n\n" +
                        "\"Peter Pan\" has two shows -afternoon show at 5:00PM and evening show at 8:00PM.\n\n" +
                        "\"Romeo and Juliet\" also has an afternoon show at 6:00PM and an evening show at 9:00PM.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("What are the shows like?");
                faq.setAnswer("Peter Pan:\n" +
                        "A lively, adventure-filled performance that runs about two hours and immerses audiences in the magic of Neverland. Playful storytelling, colorful costumes, and upbeat music make it an ideal pick for families with children aged 5 and up. " +
                        "Themes of imagination, friendship, and the joy of childhood shine through as Peter and his friends fend off pirates and discover what it means never to grow up.\n\n" +
                        "Romeo and Juliet:\n"+
                        "A faithful retelling of Shakespeare’s classic tragedy, this show spans roughly two and a half hours and appeals to teens and adults. Emotional performances, beautifully designed sets, and the timeless love story combine to explore themes of passion, " +
                        "family conflict, and fate. The production offers a poignant look at young love’s intensity—and the heartbreak that can follow when it’s tested by longstanding rivalries.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("Who are the directors and performers?");
                faq.setAnswer("Peter Pan:\n" +
                        "Directed by Mark Thompson, this magical production features Sam Brenner as Peter and Alexandra Liu as Wendy—both bringing playful energy to Neverland. Their lively performances and imaginative staging have made this show a favorite with families.\n\n" +
                        "Romeo and Juliet\n" +
                        "Eleni Papadopoulou directs this timeless Shakespeare classic with Dimitris Markos as Romeo and Maria Perakis as Juliet. They deliver a modern yet faithful interpretation, capturing the enduring romance and tragedy of Verona’s star-crossed lovers.");
                res.add(faq);
                break;

            case 5:
                // Troubleshooting & Support

                faq = new Faq();
                faq.setQuestion("UsherBot won’t understand my input. What do I do?");
                faq.setAnswer("Try rephrasing your request or use some of the suggested commands. If you’re still stuck, you can request a human agent.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("How do I speak to a real person?");
                faq.setAnswer("Ask UsherBot to connect you to a support agent (during our operating hours -Monday to Friday 8:00AM-8:00PM).\n\n" +
                        "You can also call our support line at 210-0000000.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("What if I have a more complex request?");
                faq.setAnswer("If UsherBot can’t handle your specific needs (e.g., large group bookings, special accommodations), it will offer to transfer you to a human agent or provide our support line. We’re here to help!");
                res.add(faq);
                break;

            default:
                break;
        }

        return res;
    }
}
